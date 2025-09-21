package com.amalitech.pexelhub.repository;

import com.amalitech.pexelhub.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository for Photo entities with a helper query for recent-first pagination.
 */
@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {
    /**
     * Finds a page of photos ordered by most recent first.
     *
     * @param pageable Spring Data pagination information
     * @return a page of photos
     */
    @Query("SELECT p FROM Photo p ORDER BY p.createdAt DESC")
    Page<Photo> findPhotos(Pageable pageable);
}
