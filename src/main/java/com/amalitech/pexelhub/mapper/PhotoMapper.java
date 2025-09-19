package com.amalitech.pexelhub.mapper;

import com.amalitech.pexelhub.model.Photo;
import com.amalitech.pexelhub.dto.response.PhotoResponse;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Component
public class PhotoMapper {

  private final S3Presigner s3Presigner;
  private final String bucketName;

  public PhotoMapper(S3Presigner s3Presigner,
      @Value("${aws.s3.bucket.name}") String bucketName) {
    this.s3Presigner = s3Presigner;
    this.bucketName = bucketName;
  }

  public PhotoResponse toPhotoResponse(Photo photo) {
    if (photo == null) {
      return null;
    }

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(photo.getS3Key())
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofDays(5))
        .getObjectRequest(getObjectRequest)
        .build();

    String presignedUrl = s3Presigner.presignGetObject(
        presignRequest).url().toString();

    return new PhotoResponse(presignedUrl, photo.getDescription());
  }
}
