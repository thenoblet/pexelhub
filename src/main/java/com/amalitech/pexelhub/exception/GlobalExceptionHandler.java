package com.amalitech.pexelhub.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized REST exception handling returning consistent error payloads.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles IOExceptions typically arising from file processing.
   *
   * @param ex the thrown IOException
   * @return 400 response with error details
   */
  @ExceptionHandler(IOException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, String>> handleIOException(IOException ex) {
    logger.error("IO Exception: {}", ex.getMessage(), ex);

    Map<String, String> response = new HashMap<>();
    response.put("error", "File processing error");
    response.put("message", ex.getMessage());

    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Handles exceeded upload size errors.
   *
   * @param ex the MaxUploadSizeExceededException
   * @return 413 response with error details
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
  public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException ex) {
    Map<String, String> response = new HashMap<>();
    response.put("error", "File too large");
    response.put("message", "File size exceeds the maximum allowed limit");

    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
  }

  /**
   * Catches any unhandled exceptions and returns a generic error payload.
   *
   * @param ex the unexpected exception
   * @return 500 response with generic error details
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
    logger.error("Unexpected error: {}", ex.getMessage(), ex);

    Map<String, String> response = new HashMap<>();
    response.put("error", "Internal server error");
    response.put("message", "An unexpected error occurred");

    return ResponseEntity.internalServerError().body(response);
  }
}
