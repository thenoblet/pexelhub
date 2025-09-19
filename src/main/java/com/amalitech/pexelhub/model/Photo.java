package com.amalitech.pexelhub.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "photos")
public class Photo {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(unique = true, nullable = false)
  private UUID id;

  @Column(length = 500)
  private String description;

  @Column(nullable = false, unique = true)
  private String s3Key;

  @NotNull
  private LocalDateTime createdAt;

  @NotNull
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getS3Key() {
    return s3Key;
  }

  public void setS3Key(String key) {
    this.s3Key = key;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
