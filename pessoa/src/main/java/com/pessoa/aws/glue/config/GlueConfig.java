package com.pessoa.aws.glue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.glue.GlueClient;

@Configuration
public class GlueConfig {

    /**
     * Método responsável pela configuração do bean aws do GlueClient.
     * @return GlueClient
     */
    @Bean
    public GlueClient glueClient() {
        return GlueClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}
