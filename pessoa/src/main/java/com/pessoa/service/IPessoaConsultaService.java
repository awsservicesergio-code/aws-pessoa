package com.pessoa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.dto.PessoaDTO;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IPessoaConsultaService {
    PessoaDTO buscarPessoa(String cpf) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException;
    void responder(String correlationId, PessoaDTO pessoaDTO);
}
