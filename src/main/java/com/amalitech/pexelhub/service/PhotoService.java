package com.amalitech.pexelhub.service;

import com.amalitech.pexelhub.dto.response.PhotoResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Service contract for managing photos and their storage.
 */
@Service
public interface PhotoService {
    /**
     * Uploads a photo to storage and persists metadata.
     *
     * @param file        the multipart image file
     * @param description optional description text
     */
    void uploadPhoto(MultipartFile file, String description);

    /**
     * Retrieves all photos as DTOs. Intended for administrative or non-paginated views.
     *
     * @return list of photo response DTOs
     */
    List<PhotoResponse> getAllPhotos();

    /**
     * Retrieves a paginated window of photos for infinite scrolling.
     *
     * @param offset zero-based starting index
     * @param limit  maximum number of items to return
     * @return response map with photos and pagination metadata
     */
    Map<String, Object> getPhotos(int offset, int limit);

    /**
     * Counts total number of photos persisted.
     *
     * @return total count
     */
    long getTotalPhotoCount();
}