package com.company.capa.worker;

import com.company.capa.model.Document;
import com.company.capa.repository.DocumentRepository;
import com.company.capa.service.CapaDocumentPdfService;
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
import java.util.Map;

@Component
@ConditionalOnBean(CamundaClient.class)
public class CapaDocumentWorker implements JobHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CapaDocumentWorker.class);

    private final CamundaClient camundaClient;
    private final CapaDocumentPdfService documentService;
    private final DocumentRepository documentRepository;
    private final MinioStorageService storageService;
    private JobWorker worker;

    @Value("${minio.bucket}")
    private String bucketName;

    public CapaDocumentWorker(CamundaClient camundaClient,
                              CapaDocumentPdfService documentService,
                              DocumentRepository documentRepository,
                              MinioStorageService storageService) {
        this.camundaClient = camundaClient;
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.storageService = storageService;
    }

    @PostConstruct
    public void start() {
        LOG.info("Starting CapaDocumentWorker - PDF Generation Version");
        worker = camundaClient.newWorker()
                .jobType("generate-capa-document")
                .handler(this)
                .name("capa-document-worker")
                .open();
        LOG.info("CapaDocumentWorker started successfully and listening for jobs");
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
        LOG.info("=== WORKER ACTIVATED === Generating CAPA PDF document for process instance: {}", job.getProcessInstanceKey());
        try {
            Map<String, Object> variables = job.getVariablesAsMap();

            // Generate PDF document
            LOG.info("Calling PDF service to generate document...");
            byte[] docBytes = documentService.generateCapaDocument(variables);

            // Create filename
            String capaNumber = variables.getOrDefault("capaNumber", "N/A").toString();
            String filename = String.format("CAPA_%s_%s.pdf",
                    capaNumber,
                    LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            // Build MinIO object key
            String objectKey = "documents/" + filename;

            // Upload to MinIO
            ObjectWriteResponse resp = storageService.upload(
                    docBytes,
                    bucketName,
                    objectKey,
                    "application/pdf"
            );

            // Save metadata to DB
            Document document = new Document();
            document.setFileName(filename);
            document.setFileSize((long) docBytes.length);
            document.setS3Bucket(bucketName);
            document.setS3Key(objectKey);
            document.setETag(resp.etag());
            // Versioning returns null unless bucket versioning is enabled in MinIO
            document.setS3VersionId(resp.versionId());
            Document savedDoc = documentRepository.save(document);

            LOG.info("PDF Document saved successfully with ID: {} and filename: {}", savedDoc.getId(), filename);

            // Complete the job with document metadata (avoid nulls in variables)
            Map<String, Object> completionVars = new java.util.HashMap<>();
            completionVars.put("documentId", savedDoc.getId());
            completionVars.put("documentFileName", filename);
            completionVars.put("s3Bucket", bucketName);
            completionVars.put("s3Key", objectKey);
            if (resp.etag() != null) {
                completionVars.put("eTag", resp.etag());
            }
            if (resp.versionId() != null) {
                completionVars.put("s3VersionId", resp.versionId());
            }
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
