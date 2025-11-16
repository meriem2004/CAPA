package com.company.capa.worker;

import com.company.capa.model.Document;
import com.company.capa.repository.DocumentRepository;
import com.company.capa.service.CapaDocumentService;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.client.api.worker.JobHandler;
import io.camunda.client.api.worker.JobWorker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@ConditionalOnBean(CamundaClient.class)
public class CapaDocumentWorker implements JobHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CapaDocumentWorker.class);

    private final CamundaClient camundaClient;
    private final CapaDocumentService documentService;
    private final DocumentRepository documentRepository;
    private JobWorker worker;

    public CapaDocumentWorker(CamundaClient camundaClient,
                              CapaDocumentService documentService,
                              DocumentRepository documentRepository) {
        this.camundaClient = camundaClient;
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    @PostConstruct
    public void start() {
        worker = camundaClient.newWorker()
                .jobType("generate-capa-document")
                .handler(this)
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

            // Generate Word document
            byte[] docBytes = documentService.generateCapaDocument(variables);

            // Create filename
            String capaNumber = variables.getOrDefault("capaNumber", "N/A").toString();
            String filename = String.format("CAPA_%s_%s.docx",
                    capaNumber,
                    LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

            // Persist to disk under ./capa-documents
            java.nio.file.Path dir = java.nio.file.Paths.get("capa-documents");
            java.nio.file.Files.createDirectories(dir);
            java.nio.file.Path filePath = dir.resolve(filename);
            java.nio.file.Files.write(filePath, docBytes);

            // Save metadata to DB
            Document document = new Document();
            document.setFileName(filename);
            document.setFilePath(filePath.toAbsolutePath().toString());
            document.setFileSize((long) docBytes.length);
            Document savedDoc = documentRepository.save(document);

            LOG.info("Document saved successfully with ID: {}", savedDoc.getId());

            // Complete the job with document metadata
            client.newCompleteCommand(job.getKey())
                    .variables(Map.of(
                            "documentId", savedDoc.getId(),
                            "documentFileName", filename,
                            "documentPath", filePath.toString(),
                            "documentGeneratedAt", LocalDateTime.now().toString()
                    ))
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