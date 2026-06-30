package com.pessoa.aws.sqs.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pessoa.Mapper.PessoaMapper;
import com.pessoa.aws.avro.deserializer.AvroDeserializer;
import com.pessoa.aws.avro.serializer.SerializerAvro;
import com.pessoa.aws.glue.service.GlueSchemaService;
import com.pessoa.aws.payload.AvroEnvelope;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.resources.avro.EventType;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.pessoa.resources.avro.PessoaAvro;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PessoaProducer implements IPessoaProducer{

    private final GlueSchemaService glueSchemaService;
    private final SqsTemplate sqsTemplate;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final AvroDeserializer avroDeserializer;

    /**
     * Método responsável pelo envio do payload ao aws sqs.
     * @param pessoaDTO
     * @throws JsonProcessingException
     */
    @Override
    public void enviarToSQS(PessoaDTO pessoaDTO) throws JsonProcessingException {
        String idempotencyKey = pessoaDTO.getCpf();

        try {
            PessoaAvro pessoaAvro = PessoaMapper.toAvro(pessoaDTO);
            byte[] avroBytes = SerializerAvro.serialize(pessoaAvro);

            String schemaVersionId = glueSchemaService.obterSchemaVersionId(pessoaAvro.getSchema().toString());
            AvroEnvelope envelope = AvroEnvelope.builder().schemaVersionId(schemaVersionId)
                    .payload(Base64.getEncoder().encodeToString(avroBytes)).build();

            String body = objectMapper.writeValueAsString(envelope);

            if (pessoaDTO.getEventType() != EventType.READ) {
                sqsTemplate.send(sqsSendOptions -> sqsSendOptions
                        .queue(filaSQSParaEnvioDoPayload(pessoaDTO))
                        .payload(body)
                        .header("message-group-id", "pessoa")
                        .header("message-deduplication-id", idempotencyKey)
                );
            } else {
                sqsTemplate.send(sqsSendOptions -> sqsSendOptions
                        .queue(filaSQSParaEnvioDoPayload(pessoaDTO))
                        .payload(body)
                );
            }

        } catch (Exception ex) {
            redisTemplate.delete(idempotencyKey);
            throw ex;
        }
    }

    /**
     * Método responsável por enviar o payload para a fila pessoa-dlq.fifo do aws sqs.
     * @param envelope
     * @throws JsonProcessingException
     */
    @Override
    public void enviarAvroEnvelopeToSqs(AvroEnvelope envelope, Exception ex) throws JsonProcessingException {
        PessoaDTO pessoa = desserializacao(envelope);
        String idempotencyKey = pessoa.getCpf();
        String body = objectMapper.writeValueAsString(envelope);
        String message = criarMensagemErro("pessoa-dlq.fifo", body, ex);
        sqsTemplate.send(options -> options
                .queue("pessoa-dlq.fifo")
                .payload(body)
                .header("message-group-id", "pessoa")
                .header("message-deduplication-id", idempotencyKey)
                .header("exception", ex.getClass().getSimpleName())
                .header("error-message", ex.getMessage())
                .header("consumer", "PessoaConsumer")
        );
    }

    /**
     * Método responsável pela desserialização do payload.
     * @param envelope
     * @return Pessoa
     */
    private PessoaDTO desserializacao(AvroEnvelope envelope) {
        Schema schema = glueSchemaService.buscarSchema(envelope.getSchemaVersionId());
        byte[] bytes = Base64.getDecoder().decode(envelope.getPayload());
        GenericRecord record = avroDeserializer.deserialize(bytes, schema);
        return PessoaMapper.fromAvroToPessoa(record);
    }

    /**
     * Método responsável por criar mensagem para a fila pessoa-dql.fifo
     * @param queue
     * @param payload
     * @param ex
     * @return String
     */
    private String criarMensagemErro(String queue, Object payload, Exception ex) {
        return """
            {
              "sourceQueue": "%s",
              "payload": %s,
              "error": "%s"
            }
        """.formatted(queue, payload.toString(), ex.getMessage());
    }

    /**
     * Método responsável por selecionar a fila ao qual será enviada a mensagem de acordo com o tipo de evento.
     * @param pessoaDTO
     * @return String
     */
    private String filaSQSParaEnvioDoPayload(PessoaDTO pessoaDTO){
        String retorno = null;
        switch (pessoaDTO.getEventType()){
            case CREATE -> {retorno = "pessoa-create.fifo";}
            case READ -> {retorno = "pessoa-read";}
            case UPDATE -> {retorno = "pessoa-update.fifo";}
            case DELETE -> {retorno = "pessoa-delete.fifo";}
        }
        return retorno;
    }
}
