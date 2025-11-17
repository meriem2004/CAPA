package com.company.capa.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey
    ) {
        if (url == null || accessKey == null || secretKey == null) {
            throw new IllegalStateException(
                "Missing MinIO configuration. Please set MINIO_URL, MINIO_ACCESS_KEY and MINIO_SECRET_KEY environment variables or corresponding Spring properties."
            );
        }
        // Trim to avoid signature mismatch due to accidental spaces/newlines
        String endpoint = url.trim();
        String ak = accessKey.trim();
        String sk = secretKey.trim();

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(ak, sk)
                .build();
    }
}