package com.amalitech.pexelhub.mapper;

import com.amalitech.pexelhub.dto.response.PhotoResponse;
import com.amalitech.pexelhub.model.Photo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

/**
 * Maps Photo entities to PhotoResponse DTOs, generating short-lived presigned URLs
 * for direct S3 access.
 */
@Component
public class PhotoMapper {
    private final S3Presigner s3Presigner;
    private final String bucketName;

    /**
     * @param s3Presigner S3 presigner used to create presigned GET URLs
     * @param bucketName  target S3 bucket name
     */
    public PhotoMapper(S3Presigner s3Presigner, @Value("${aws.s3.bucket.name}") String bucketName) {
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    /**
     * Converts a Photo entity to its DTO, embedding a presigned URL for the file.
     *
     * @param photo the photo entity
     * @return DTO representing the photo
     */
    public PhotoResponse toPhotoResponse(Photo photo) {
        String presignedUrl = generatePresignedUrl(photo.getS3Key());
        return new PhotoResponse(presignedUrl, photo.getDescription());
    }

    /**
     * Generates a time-limited presigned URL for reading an object from S3.
     *
     * @param key the S3 object key
     * @return URL string valid for a limited duration
     */
    private String generatePresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
}
