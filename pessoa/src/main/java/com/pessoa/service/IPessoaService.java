package com.pessoa.service;

import com.pessoa.dto.PessoaDTO;
import java.util.List;

public interface IPessoaService {
    List<PessoaDTO> save(List<PessoaDTO> pessoaDTO);
}
