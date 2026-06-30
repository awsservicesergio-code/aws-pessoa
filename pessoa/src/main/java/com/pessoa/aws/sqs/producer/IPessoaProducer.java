package com.pessoa.aws.sqs.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.aws.payload.AvroEnvelope;
import com.pessoa.dto.PessoaDTO;

public interface IPessoaProducer {
    void enviarToSQS(PessoaDTO pessoaDTO) throws JsonProcessingException;
    void enviarAvroEnvelopeToSqs(AvroEnvelope avroEnvelope, Exception ex) throws JsonProcessingException;
}
