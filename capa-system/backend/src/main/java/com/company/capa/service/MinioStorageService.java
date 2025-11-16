package com.company.capa.service;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class MinioStorageService {

    private final MinioClient minioClient;

    public MinioStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Upload a byte array as an object to MinIO.
     * Ensures the bucket exists, creates it if missing.
     * Returns ObjectWriteResponse containing eTag and versionId.
     */
    public ObjectWriteResponse upload(byte[] data, String bucket, String objectName, String contentType) throws Exception {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                return minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectName)
                                .contentType(contentType)
                                .stream(bais, data.length, -1)
                                .build()
                );
            }
        } catch (MinioException e) {
            throw new RuntimeException("MinIO upload failed: " + e.getMessage(), e);
        }
    }
}