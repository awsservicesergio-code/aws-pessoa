package com.pessoa.aws.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pessoa.Mapper.PessoaMapper;
import com.pessoa.aws.avro.SerializerAvro;
import com.pessoa.aws.glue.service.GlueSchemaService;
import com.pessoa.aws.payload.AvroEnvelope;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.error.exceptions.SqsErrorException;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.pessoa.resources.avro.PessoaAvro;

import java.time.Duration;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PessoaProducer implements IPessoaProducer{

    private final GlueSchemaService glueSchemaService;
    private final SqsTemplate sqsTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void enviarToSQS(PessoaDTO pessoaDTO) throws JsonProcessingException {

        String idempotencyKey = pessoaDTO.getCpf();
        Boolean isIdempotent = redisTemplate.opsForValue()
                .setIfAbsent(idempotencyKey, "PROCESSANDO", Duration.ofHours(1));

        if (Boolean.FALSE.equals(isIdempotent)) {
           throw new SqsErrorException("CPF já cadastrado.");
        }

        try {
            PessoaAvro pessoaAvro = PessoaMapper.toAvro(pessoaDTO);
            byte[] avroBytes = SerializerAvro.serialize(pessoaAvro);

            String schemaVersionId = glueSchemaService.obterSchemaVersionId(pessoaAvro.getSchema().toString());
            AvroEnvelope envelope = AvroEnvelope.builder().schemaVersionId(schemaVersionId)
                    .payload(Base64.getEncoder().encodeToString(avroBytes)).build();

            String body = objectMapper.writeValueAsString(envelope);


            sqsTemplate.send(sqsSendOptions -> sqsSendOptions
                    .queue("pessoa.fifo")
                    .payload(body)
                    .header("message-group-id", "pessoa")
                    .header("message-deduplication-id", idempotencyKey)
            );
        } catch (Exception ex) {
            redisTemplate.delete(idempotencyKey);
            throw ex;
        }

    }
}
