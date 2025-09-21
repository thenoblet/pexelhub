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

/**
 * REST controller exposing endpoints for photo upload and retrieval.
 * <p>
 * Base path: /api/v1
 */
@RestController
@RequestMapping("/api/v1/")
public class PhotoController {

    private final PhotoService photoService;

    /**
     * Constructs the controller with the required PhotoService.
     *
     * @param photoService service handling photo operations
     */
    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    /**
     * Uploads an image to storage and persists its metadata.
     *
     * @param file        the image file to upload (must be a non-empty image/* type)
     * @param description optional description for the image; empty by default
     * @return 200 OK when uploaded; 400 on validation failure; 500 on server error
     * @throws IOException if reading the multipart stream fails
     */
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

        try {
            photoService.uploadPhoto(file, description);
            return ResponseEntity.ok("Photo uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    /**
     * Returns a paginated chunk of photos for infinite scrolling.
     *
     * @param offset zero-based starting index of the page window
     * @param limit  maximum number of items to return
     * @return map containing keys: photos (List<PhotoResponse>), hasMore (boolean),
     * totalElements (long), currentOffset (int), nextOffset (int)
     */
    @GetMapping("/photos/more")
    public ResponseEntity<Map<String, Object>> getMorePhotos(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit) {

        try {
            Map<String, Object> result = photoService.getPhotos(offset, limit);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}