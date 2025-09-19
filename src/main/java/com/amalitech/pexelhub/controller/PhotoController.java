package com.amalitech.pexelhub.controller;

import com.amalitech.pexelhub.service.PhotoService;
import com.amalitech.pexelhub.dto.response.PhotoResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class PhotoController {

  private final PhotoService photoService;

  public PhotoController(PhotoService photoService) {
    this.photoService = photoService;
  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadPhoto(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "description", required = false, defaultValue = "") String description)
      throws IOException {

    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("File cannot be empty");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      return ResponseEntity.badRequest().body("Only image files are allowed");
    }

    photoService.uploadPhoto(file, description);
    return ResponseEntity.ok("Upload successful");
  }

    @GetMapping("/photos/more")
    public ResponseEntity<Map<String, Object>> getMorePhotos(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit) {
        Map<String, Object> response = photoService.getPhotos(offset, limit);
        return ResponseEntity.ok(response);
    }
}