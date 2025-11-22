package com.company.capa.service;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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

    public InputStream download(String bucket, String objectName) throws Exception {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException("MinIO download failed: " + e.getMessage(), e);
        }
    }

    public String getPresignedUrl(String bucket, String objectName, int expirySeconds) throws Exception {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .expiry(expirySeconds)
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException("MinIO presigned URL failed: " + e.getMessage(), e);
        }
    }
}
