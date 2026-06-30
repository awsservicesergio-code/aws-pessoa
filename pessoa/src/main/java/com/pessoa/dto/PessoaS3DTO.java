package com.pessoa.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pessoa.config.TimestampSerializer;
import com.pessoa.resources.avro.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PessoaS3DTO {
    private String id;
    private String nome;
    private String cpf;
    private String s3Key;
    private String s3PreSigner;
    private String eventId;
    private EventType eventType;
    @JsonSerialize(using = TimestampSerializer.class)
    private Long timestamp;

}
