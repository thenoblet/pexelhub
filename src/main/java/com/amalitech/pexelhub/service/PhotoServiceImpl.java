package com.amalitech.pexelhub.service;

import com.amalitech.pexelhub.dto.response.PhotoResponse;
import com.amalitech.pexelhub.model.Photo;
import com.amalitech.pexelhub.repository.PhotoRepository;
import com.amalitech.pexelhub.mapper.PhotoMapper;
import com.amalitech.pexelhub.exception.FileUploadException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.util.HashMap;
import java.util.UUID;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PhotoServiceImpl implements PhotoService {

  private final S3Client s3Client;
  private final PhotoRepository photoRepository;
  private final PhotoMapper photoMapper;
  private final String bucketName;

  public PhotoServiceImpl(
      PhotoRepository photoRepository,
      S3Presigner s3Presigner,
      S3Client s3Client,
      PhotoMapper photoMapper,
      @Value("${aws.s3.bucket.name}") String bucketName) {
    this.photoRepository = photoRepository;
    this.s3Client = s3Client;
    this.photoMapper = photoMapper;
    this.bucketName = bucketName;
  }

  @Override
  public void uploadPhoto(MultipartFile file, String description) {
    try {
      String originalFilename = file.getOriginalFilename();
      if (originalFilename == null || originalFilename.contains("..")) {
        throw new FileUploadException("Invalid file name");
      }

      String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
      String s3Key = "images/" + UUID.randomUUID() + "-" + sanitizedFilename;

      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(s3Key)
          .contentType(file.getContentType())
          .build();

      s3Client.putObject(putObjectRequest,
          RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

      Photo photo = new Photo();
      photo.setDescription(description);
      photo.setS3Key(s3Key);
      photoRepository.save(photo);

    } catch (IOException ex) {
      throw new FileUploadException("Failed to upload file: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<PhotoResponse> getAllPhotos() {
    return photoRepository.findAll().stream()
        .map(photoMapper::toPhotoResponse)
        .collect(Collectors.toList());
  }

  @Override
  public Map<String, Object> getPhotos(int offset, int limit) {
    // Page number is calculated from the offset
    Pageable pageable = PageRequest.of(offset / limit, limit);
    Page<Photo> photoPage = photoRepository.findAll(pageable);

    List<PhotoResponse> photoResponses = photoPage.getContent().stream()
            .map(photoMapper::toPhotoResponse)
            .collect(Collectors.toList());

    Map<String, Object> response = new HashMap<>();
    response.put("photos", photoResponses);
    response.put("hasMore", photoPage.hasNext());

    return response;
  }
}