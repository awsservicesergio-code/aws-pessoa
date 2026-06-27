package com.pessoa.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import java.net.URI;

@Configuration
public class AwsSqsConfig {

    /**
     * Método responsável pelo bean local do localstack para SqsAsyncClient.
     * @return SqsAsyncClient
     */
    @Bean
    @Profile("local")
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .endpointOverride(
                        URI.create("http://localhost:4566")
                )
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        "test",
                                        "test"
                                )
                        )
                )
                .build();
    }

    /**
     * Método responsável pelo bean aws para SqsAsyncClient.
     * @return SqsAsyncClient
     */
    @Bean
    @Profile("aws")
    public SqsAsyncClient sqsAsyncClientAws() {
        return SqsAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}
