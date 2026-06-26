package com.pessoa.Mapper;

import com.pessoa.resources.avro.PessoaAvro;
import com.pessoa.dto.PessoaDTO;

public class PessoaMapper {
    public static PessoaAvro toAvro(PessoaDTO dto) {
        return PessoaAvro.newBuilder()
                .setId(dto.getId())
                .setNome(dto.getNome())
                .setCpf(dto.getCpf())
                .build();
    }
}
