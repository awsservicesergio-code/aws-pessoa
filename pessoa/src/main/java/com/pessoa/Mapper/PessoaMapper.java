package com.pessoa.Mapper;

import com.pessoa.resources.avro.PessoaAvro;
import com.pessoa.dto.PessoaDTO;
import java.time.Instant;


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
                .setEventId(dto.getEventId())
                .setEventType(dto.getEventType())
                .setTimestamp(dto.getTimestamp() != null ? Instant.ofEpochMilli(dto.getTimestamp()) : null)
                .build();
    }

}
