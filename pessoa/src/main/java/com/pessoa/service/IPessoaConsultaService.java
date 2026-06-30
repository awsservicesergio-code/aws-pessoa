package com.pessoa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.dto.PessoaS3DTO;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IPessoaConsultaService {
    PessoaS3DTO buscarPessoa(String cpf) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException;
    void responder(String correlationId, PessoaDTO pessoaDTO);
}
