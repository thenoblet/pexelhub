package com.amalitech.pexelhub.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * JPA entity representing a photo stored in S3 with metadata persisted in the database.
 */
@Entity
@Table(name = "photos")
public class Photo {

  /** Primary key identifier. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(unique = true, nullable = false)
  private UUID id;

  /** Optional human-readable description of the photo. */
  @Column(length = 500)
  private String description;

  /** Unique S3 object key for the stored image. */
  @Column(nullable = false, unique = true)
  private String s3Key;

  /** Timestamp when the entity was created. */
  @NotNull
  private LocalDateTime createdAt;

  /** Timestamp when the entity was last updated. */
  @NotNull
  private LocalDateTime updatedAt;

  /**
   * Lifecycle hook invoked before the entity is first persisted.
   * Initializes creation and update timestamps.
   */
  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Lifecycle hook invoked before the entity is updated.
   * Refreshes the update timestamp.
   */
  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  /** @return the entity id */
  public UUID getId() {
    return id;
  }

  /** @param id the entity id */
  public void setId(UUID id) {
    this.id = id;
  }

  /** @return the photo description */
  public String getDescription() {
    return description;
  }

  /** @param description description text */
  public void setDescription(String description) {
    this.description = description;
  }

  /** @return the S3 object key */
  public String getS3Key() {
    return s3Key;
  }

  /** @param key the S3 object key */
  public void setS3Key(String key) {
    this.s3Key = key;
  }

  /** @return creation timestamp */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /** @param createdAt creation timestamp */
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /** @return last update timestamp */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /** @param updatedAt last update timestamp */
  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
