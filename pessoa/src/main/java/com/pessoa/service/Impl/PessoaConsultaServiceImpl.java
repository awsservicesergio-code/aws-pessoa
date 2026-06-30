package com.pessoa.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pessoa.Mapper.PessoaMapper;
import com.pessoa.aws.s3.service.IS3StorageService;
import com.pessoa.aws.sqs.producer.IPessoaProducer;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.dto.PessoaS3DTO;
import com.pessoa.resources.avro.EventType;
import com.pessoa.service.IPessoaConsultaService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class PessoaConsultaServiceImpl implements IPessoaConsultaService {

    private final IPessoaProducer pessoaProducer;
    private final ObjectMapper mapper;
    private final Validator validator;
    private final IS3StorageService s3StorageService;
    private final Map<String, CompletableFuture<PessoaDTO>> pendentes = new ConcurrentHashMap<>();


    /**
     * Método responsável por buscar Pessoa e arquivo pelo cpf da Pessoa no DynamoDB.
     * @param cpf
     * @return PessoaDTO
     */
    @Override
    public PessoaS3DTO buscarPessoa(String cpf) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {

        String uuid = UUID.randomUUID().toString();
        JsonNode jsonNode = mapper.readTree(cpf);
        String valorCpf = jsonNode.get("cpf").asText();

        PessoaDTO pessoaDTO = PessoaDTO.builder() //Para realizar o teste e envio do cpf apenas como objeto PessoaDTO.
                .id(uuid)
                .cpf(valorCpf)
                .nome("John Doe")
                .s3Key(cpf + "/testando.pdf")
                .eventId(uuid)
                .eventType(EventType.READ)
                .timestamp(System.currentTimeMillis())
                .build();

        var violations = validator.validate(pessoaDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        CompletableFuture<PessoaDTO> future = new CompletableFuture<>();
        pendentes.put(valorCpf, future);

        pessoaProducer.enviarToSQS(pessoaDTO);

        try {
            PessoaDTO dto =  future.get(20, TimeUnit.SECONDS);
            String s3Presigner = s3StorageService.chamarUrlDownload(dto.getS3Key());
            PessoaS3DTO pessoaS3DTO = PessoaMapper.PessoaDTOToPessoaS3DTO(dto, s3Presigner);
            return pessoaS3DTO;
        } finally {
            pendentes.remove(valorCpf);
        }
    }

    @Override
    public void responder(String correlationId, PessoaDTO pessoaDTO) {
        CompletableFuture<PessoaDTO> future = pendentes.get(correlationId);
        if (future != null) {
            future.complete(pessoaDTO);
        }
    }

}
