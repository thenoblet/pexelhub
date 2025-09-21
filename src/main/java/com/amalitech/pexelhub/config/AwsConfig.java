package com.amalitech.pexelhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Spring configuration for AWS SDK clients used in the application.
 */
@Configuration
public class AwsConfig {

    /**
     * Creates a synchronous S3 client.
     *
     * @return configured S3Client
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
    }

    /**
     * Creates an S3 presigner for generating presigned URLs.
     *
     * @return configured S3Presigner
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
    }
}