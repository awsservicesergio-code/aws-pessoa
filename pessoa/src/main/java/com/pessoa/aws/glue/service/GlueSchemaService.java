package com.pessoa.aws.glue.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.glue.GlueClient;
import software.amazon.awssdk.services.glue.model.GetSchemaVersionResponse;
import software.amazon.awssdk.services.glue.model.RegisterSchemaVersionRequest;
import software.amazon.awssdk.services.glue.model.RegisterSchemaVersionResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class GlueSchemaService {

    private static final String REGISTRY_NAME = "pessoa-registry";
    private static final String SCHEMA_NAME = "pessoa-aws-glue-schema-registry";

    private final GlueClient glueClient;
    private final Map<String, Schema> cache = new ConcurrentHashMap<>();

    /**
     * Método responsável por obter o Schema Registry na aws do AWS GLUE Schema Registry.
     * @param schemaDefinition
     * @return String
     */
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

    /**
     * Método responsável por obter o schema registry do AWS GLUE Schema Registry da aws do consumer.
     * @param versionId
     * @return Schema
     */
    public Schema buscarSchema(String versionId) {
        return cache.computeIfAbsent(versionId, this::buscar);
    }

    /**
     * Método responsável por auxiliar a obter o schema registry do AWS GLUE Schema Registry da aws no consumer.
     * @param versionId
     * @return
     */
    private Schema buscar(String versionId) {
        GetSchemaVersionResponse response = glueClient.getSchemaVersion(builder ->
                builder.schemaVersionId(versionId)
        );
        return new Schema.Parser().parse(response.schemaDefinition());
    }
}
