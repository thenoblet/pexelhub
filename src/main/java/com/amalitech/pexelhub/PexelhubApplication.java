package com.amalitech.pexelhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Pexelhub Spring Boot application.
 */
@SpringBootApplication
public class PexelhubApplication {

  /**
   * Boots the Spring application context.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(PexelhubApplication.class, args);
  }
}
