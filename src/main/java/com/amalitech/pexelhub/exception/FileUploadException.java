package com.amalitech.pexelhub.exception;

/**
 * Unchecked exception thrown when a file upload fails due to validation or I/O issues.
 */
public class FileUploadException extends RuntimeException {
  /**
   * Creates a new exception with message only.
   * @param message description of the error
   */
  public FileUploadException(String message) {
    super(message);
  }

  /**
   * Creates a new exception with message and underlying cause.
   * @param message description of the error
   * @param cause underlying exception
   */
  public FileUploadException(String message, Throwable cause) {
    super(message, cause);
  }
}
