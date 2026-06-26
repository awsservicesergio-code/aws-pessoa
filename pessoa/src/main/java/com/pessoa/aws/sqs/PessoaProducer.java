package com.pessoa.aws.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pessoa.Mapper.PessoaMapper;
import com.pessoa.aws.avro.SerializerAvro;
import com.pessoa.aws.glue.service.GlueSchemaService;
import com.pessoa.aws.payload.AvroEnvelope;
import com.pessoa.dto.PessoaDTO;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.pessoa.resources.avro.PessoaAvro;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PessoaProducer {

    private final GlueSchemaService glueSchemaService;
    private final SqsTemplate sqsTemplate;

    public void enviar(PessoaDTO pessoaDTO) throws JsonProcessingException {
        PessoaAvro pessoaAvro = PessoaMapper.toAvro(pessoaDTO);
        byte[] avroBytes = SerializerAvro.serialize(pessoaAvro);
        String schemaVersionId = glueSchemaService.obterSchemaVersionId(pessoaAvro.getSchema().toString());
        AvroEnvelope envelope = AvroEnvelope.builder().schemaVersionId(schemaVersionId)
                .payload(Base64.getEncoder().encodeToString(avroBytes)).build();
        sqsTemplate.send("pessoa.fifo", new ObjectMapper().writeValueAsString(envelope));
    }
}
