package com.pessoa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.dto.PessoaDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IPessoaService {
    PessoaDTO save(String pessoa, MultipartFile arquivo) throws IOException;
    PessoaDTO buscarPessoa(String cpf) throws JsonProcessingException;
}
