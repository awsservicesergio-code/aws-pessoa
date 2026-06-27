package com.pessoa.service;

import com.pessoa.dto.PessoaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPessoaService {
    PessoaDTO save(String pessoa, MultipartFile arquivo) throws IOException;
}
