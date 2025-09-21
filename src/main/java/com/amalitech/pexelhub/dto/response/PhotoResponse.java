package com.amalitech.pexelhub.dto.response;

/**
 * DTO representing a photo resource exposed to clients.
 *
 * @param s3Url       a presigned URL to access the image
 * @param description optional description text
 */
public record PhotoResponse(String s3Url, String description) {
}