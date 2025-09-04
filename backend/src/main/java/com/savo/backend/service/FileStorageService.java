package com.savo.backend.service;

import com.savo.backend.config.S3Properties;
import com.savo.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public FileStorageService(S3Client s3Client, S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
    }

    public String uploadFile(MultipartFile file, String userId, String folder) {
        validateFile(file);

        String s3Key = generateS3Key(userId, folder, file.getOriginalFilename());

        try {
            Map<String, String> metadata = createFileMetadata(file, userId);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .metadata(metadata)
                    .serverSideEncryption(ServerSideEncryption.AES256)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            logger.info("Successfully upload file to S3: bucket={}, key={}", s3Properties.getBucket(), s3Key);
            return s3Key;
        } catch (IOException e) {
            logger.error("Failed to read file for upload: {}", s3Key, e);
            throw new ValidationException("Failed to read file: " + e.getMessage());
        } catch (S3Exception e) {
            logger.error("S3 upload failed: bucket={}, key={}, error={}", s3Properties.getBucket(), s3Key, e.awsErrorDetails().errorCode(), e);
            throw new ValidationException("File upload failed: " + e.awsErrorDetails().errorMessage());
        }
    }

    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Successfully delete file from S3: bucket={}, key={}", s3Properties.getBucket(), s3Key);
        } catch (S3Exception e) {
            logger.error("Failed to delete file from S3: bucket={}, key={}, error={}", s3Properties.getBucket(), s3Key, e.awsErrorDetails().errorMessage(), e);
        }
    }

    public String getPresignedDownloadUrl(String s3Key) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(s3Properties.getUrlExpirationMinutes()))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();

        } catch (S3Exception e) {
            logger.error("Failed to generate presigned URL: key={}, error={}", s3Key, e.awsErrorDetails().errorMessage(), e);
            throw new ValidationException("Failed to generate download URL");
        }
    }
}
