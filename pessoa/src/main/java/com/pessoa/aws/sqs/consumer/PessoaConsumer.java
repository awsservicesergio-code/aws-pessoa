package com.pessoa.aws.sqs.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.aws.avro.deserializer.AvroDeserializer;
import com.pessoa.Mapper.PessoaMapper;
import com.pessoa.aws.glue.service.GlueSchemaService;
import com.pessoa.aws.payload.AvroEnvelope;
import com.pessoa.aws.sqs.producer.IPessoaProducer;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.service.IPessoaConsultaService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PessoaConsumer {

    private final AvroDeserializer avroDeserializer;
    private final GlueSchemaService glueSchemaService;
    private final IPessoaConsultaService pessoaConsultaService;
    private final IPessoaProducer pessoaProducer;

    /**
     * Método responsável por escutar o aws sqs e buscar pessoa pelo cpf no DynamoDB bem como o s3 Pre-signer.
     * @param envelope
     */
    @SqsListener("pessoa-read-back")
    public void buscarPessoaPorCpf(AvroEnvelope envelope) throws JsonProcessingException {
        try {
            PessoaDTO pessoaDTO = desserializacao(envelope);
            pessoaConsultaService.responder(pessoaDTO.getCpf(), pessoaDTO); //Envia para o PessoaResource
        } catch (Exception ex) {
            pessoaProducer.enviarAvroEnvelopeToSqs(envelope, ex);
        }

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
}
