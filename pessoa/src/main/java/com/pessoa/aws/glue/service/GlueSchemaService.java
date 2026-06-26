package com.pessoa.aws.glue.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.RegisterSchemaVersionRequest;
import software.amazon.awssdk.services.glue.model.RegisterSchemaVersionResponse;

@Service
@RequiredArgsConstructor
public class GlueSchemaService {

    private static final String REGISTRY_NAME = "pessoa-registry";
    private static final String SCHEMA_NAME = "pessoa-aws-glue-schema-registry";

    private final GlueClient glueClient;

    public String obterSchemaVersionId(String schemaDefinition) {
        RegisterSchemaVersionRequest request =
                RegisterSchemaVersionRequest.builder()
                        .schemaId(builder -> builder
                                .registryName(REGISTRY_NAME)
                                .schemaName(SCHEMA_NAME))
                        .schemaDefinition(schemaDefinition)
                        .build();
        RegisterSchemaVersionResponse response =
                glueClient.registerSchemaVersion(request);
        return response.schemaVersionId();
    }
}
