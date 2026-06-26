package com.pessoa.aws.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvroEnvelope {
    private String schemaVersionId;
    private String payload;
}
