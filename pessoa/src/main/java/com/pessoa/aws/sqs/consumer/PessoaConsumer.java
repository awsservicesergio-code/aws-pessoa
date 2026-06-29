package com.pessoa.aws.sqs.consumer;

import com.pessoa.aws.avro.deserializer.AvroDeserializer;
import com.pessoa.Mapper.PessoaMapper;
import com.pessoa.aws.glue.service.GlueSchemaService;
import com.pessoa.aws.payload.AvroEnvelope;
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

    /**
     * Método responsável por escutar o aws sqs ebuscar pessoa pelo cpf no DynamoDB.
     * @param envelope
     */
    @SqsListener("pessoa-read-back")
    public void buscarPessoaPorCpf(AvroEnvelope envelope) {
        PessoaDTO pessoaDTO = desserializacao(envelope);
        System.out.println("RECEBI DO SQS -> " + pessoaDTO);
        pessoaConsultaService.responder(pessoaDTO.getCpf(), pessoaDTO); //Envia para o PessoaResource
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
