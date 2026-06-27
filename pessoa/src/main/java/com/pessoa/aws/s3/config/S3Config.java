package com.pessoa.aws.s3.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties properties;

    @Bean
    public S3Client s3Client() {S3ClientBuilder builder = S3Client.builder().region(Region.of(properties.getRegion()));
        if (properties.getEndpoint() != null && !properties.getEndpoint().isBlank()) {
            builder
                    .endpointOverride(URI.create(properties.getEndpoint()))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                            properties.getAccessKey(),
                                            properties.getSecretKey()
                                    )
                            ))
                    .forcePathStyle(properties.isPathStyleAccess());
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        return builder.build();
    }
}