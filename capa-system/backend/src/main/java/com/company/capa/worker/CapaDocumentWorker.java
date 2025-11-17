package com.company.capa.worker;

import com.company.capa.model.Document;
import com.company.capa.repository.DocumentRepository;
import com.company.capa.service.CapaDocumentService;
import com.company.capa.service.MinioStorageService;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.api.worker.JobHandler;
import io.camunda.client.api.worker.JobWorker;
import io.minio.ObjectWriteResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

@Component
@ConditionalOnBean(CamundaClient.class)
public class CapaDocumentWorker implements JobHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CapaDocumentWorker.class);

    private final CamundaClient camundaClient;
    private final CapaDocumentService documentService;
    private final DocumentRepository documentRepository;
    private final MinioStorageService storageService;
    private JobWorker worker;

    @Value("${minio.bucket}")
    private String bucketName;

    public CapaDocumentWorker(CamundaClient camundaClient,
                              CapaDocumentService documentService,
                              DocumentRepository documentRepository,
                              MinioStorageService storageService) {
        this.camundaClient = camundaClient;
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.storageService = storageService;
    }

    @PostConstruct
    public void start() {
        worker = camundaClient.newWorker()
                .jobType("generate-capa-document")
                .handler(this)
                .timeout(Duration.ofMinutes(10))
                .maxJobsActive(10)
                .pollInterval(Duration.ofMillis(200))
                .name("capa-document-worker")
                .open();
    }

    @PreDestroy
    public void stop() {
        if (worker != null) {
            try {
                worker.close();
                LOG.info("CapaDocumentWorker closed.");
            } catch (Exception e) {
                LOG.warn("Error closing CapaDocumentWorker", e);
            }
        }
    }

    @Override
    public void handle(final JobClient client, final ActivatedJob job) {
        LOG.info("Generating CAPA document for process instance: {}", job.getProcessInstanceKey());
        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // Generate PDF directly from variables
            byte[] pdfBytes = documentService.generateCapaPdf(variables);

            // Create filename (PDF)
            String capaNumber = variables.getOrDefault("capaNumber", "N/A").toString();
            String filename = String.format("CAPA_%s_%s.pdf",
                    capaNumber,
                    LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            // Build MinIO object key
            String objectKey = "documents/" + filename;

            // Upload to MinIO (PDF)
            ObjectWriteResponse resp = storageService.upload(
                    pdfBytes,
                    bucketName,
                    objectKey,
                    "application/pdf"
            );

            // Save metadata to DB (PDF)
            Document document = new Document();
            document.setFileName(filename);
            document.setFileSize((long) pdfBytes.length);
            document.setS3Bucket(bucketName);
            document.setS3Key(objectKey);
            document.setETag(resp.etag());
            document.setS3VersionId(resp.versionId());
            Document savedDoc = documentRepository.save(document);

            LOG.info("PDF saved successfully: ID={}", savedDoc.getId());

            // Complete the job with document metadata (PDF only)
            Map<String, Object> completionVars = new HashMap<>();
            completionVars.put("documentId", savedDoc.getId());
            completionVars.put("documentFileName", filename);
            completionVars.put("s3Bucket", bucketName);
            completionVars.put("s3Key", objectKey);
            completionVars.put("eTag", resp.etag());
            completionVars.put("s3VersionId", Objects.toString(resp.versionId(), ""));
            completionVars.put("pdfDocumentId", savedDoc.getId());
            completionVars.put("pdfFileName", filename);
            completionVars.put("pdfS3Key", objectKey);
            completionVars.put("pdfETag", resp.etag());
            completionVars.put("pdfS3VersionId", Objects.toString(resp.versionId(), ""));
            completionVars.put("documentGeneratedAt", LocalDateTime.now().toString());

            client.newCompleteCommand(job.getKey())
                    .variables(completionVars)
                    .send()
                    .join();
        } catch (Exception e) {
            LOG.error("Error generating CAPA document", e);
            // Fail the job with no retries (adjust as needed)
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage("Failed to generate document: " + e.getMessage())
                    .send()
                    .join();
        }
    }
}