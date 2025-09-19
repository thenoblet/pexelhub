package com.amalitech.pexelhub.service;

import com.amalitech.pexelhub.dto.response.PhotoResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface PhotoService {
  void uploadPhoto(MultipartFile file, String description);

  List<PhotoResponse> getAllPhotos();

  Map<String, Object> getPhotos(int offset, int limit);
}
