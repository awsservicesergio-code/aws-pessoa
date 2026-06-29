package com.pessoa.Mapper;

import com.pessoa.resources.avro.PessoaAvro;
import com.pessoa.dto.PessoaDTO;
import org.apache.avro.generic.GenericRecord;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

    /**
     * Método responsável por converter GenericRecord em PessoaDTO.
     * @param record
     * @return PessoaDTO
     */
    public static PessoaDTO fromAvroToPessoa(GenericRecord record) {
        return PessoaDTO.builder()
                .id(record.get("id") != null ? record.get("id").toString() : null)
                .nome(record.get("nome") != null ? record.get("nome").toString() : null)
                .cpf(record.get("cpf") != null ? record.get("cpf").toString() : null)
                .s3Key(record.get("s3Key") != null ? record.get("s3Key").toString() : null)
                .eventId(record.get("eventId") != null ? record.get("eventId").toString() : null)
                .eventType(record.get("eventType") != null ? (com.pessoa.resources.avro.EventType) record.get("eventType") : null)
                .timestamp(record.get("timestamp") != null ? converterObjectTimestampToLong(record.get("timestamp")) : null)
                .build();

    }

    /**
     * Método reponsável por converter timestamp do tipo Long para data do tipo String.
     * @param timestamp
     * @return String
     */
    public static String converterLongTimestampToString(Object timestamp){
        Instant instant = (Instant) timestamp;
        LocalDateTime dataLocal = LocalDateTime.ofInstant(
                instant,
                ZoneId.of("America/Sao_Paulo")
        );
        return dataLocal.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    /**
     * Método reponsável por converter timestamp do tipo Object para data do tipo long.
     * @param timestamp
     * @return Long
     */
    public static Long converterObjectTimestampToLong(Object timestamp){
        Instant instant = (Instant) timestamp;
        return instant.toEpochMilli();
    }

}
