package com.pessoa.aws.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.dto.PessoaDTO;

public interface IPessoaProducer {
    void enviarToSQS(PessoaDTO pessoaDTO) throws JsonProcessingException;
}
