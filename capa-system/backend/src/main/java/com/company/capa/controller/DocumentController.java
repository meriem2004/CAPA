package com.company.capa.controller;

import com.company.capa.model.Document;
import com.company.capa.repository.DocumentRepository;
import com.company.capa.service.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentRepository documentRepository;
    private final MinioStorageService storageService;
    @Value("${minio.bucket}")
    private String defaultBucket;

    @GetMapping
    public List<Document> list() {
        return documentRepository.findAll();
    }

    @GetMapping("/{id}/signed-url")
    public Map<String, String> getSignedUrl(@PathVariable Long id, @RequestParam(defaultValue = "3600") int expirySeconds) throws Exception {
        Document doc = documentRepository.findById(id).orElseThrow();
        String bucket = doc.getS3Bucket() != null && !doc.getS3Bucket().isBlank() ? doc.getS3Bucket() : defaultBucket;
        String key = doc.getS3Key();
        if (bucket == null || bucket.isBlank() || key == null || key.isBlank()) {
            throw new IllegalArgumentException("Missing bucket or object key for document " + id);
        }
        String url = storageService.getPresignedUrl(bucket, key, expirySeconds);
        return Map.of("url", url);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id) throws Exception {
        Document doc = documentRepository.findById(id).orElseThrow();
        String bucket = doc.getS3Bucket() != null && !doc.getS3Bucket().isBlank() ? doc.getS3Bucket() : defaultBucket;
        String key = doc.getS3Key();
        if (bucket == null || bucket.isBlank() || key == null || key.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        InputStream is = storageService.download(bucket, key);
        String filename = doc.getFileName();
        MediaType mediaType;
        String lower = filename != null ? filename.toLowerCase() : "";
        if (lower.endsWith(".pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        } else if (lower.endsWith(".docx")) {
            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(new InputStreamResource(is));
    }
}
