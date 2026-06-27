package com.pessoa.Mapper;

import com.pessoa.resources.avro.PessoaAvro;
import com.pessoa.dto.PessoaDTO;

public class PessoaMapper {

    /**
     * Método responsável mapear classe PessoaDTO para PessoaAvro.
     * @param dto
     * @return PessoaAvro
     */
    public static PessoaAvro toAvro(PessoaDTO dto) {
        return PessoaAvro.newBuilder()
                .setId(dto.getId())
                .setNome(dto.getNome())
                .setCpf(dto.getCpf())
                .setS3Key(dto.getS3Key())
                .build();
    }
}
