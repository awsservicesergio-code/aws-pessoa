package com.pessoa.aws.sqs.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.dto.PessoaDTO;

public interface IPessoaProducer {
    void enviarToSQS(PessoaDTO pessoaDTO) throws JsonProcessingException;
}
