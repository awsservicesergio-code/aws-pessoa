package com.pessoa.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pessoa.aws.s3.service.IS3StorageService;
import com.pessoa.aws.sqs.IPessoaProducer;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.resources.avro.EventType;
import com.pessoa.service.IPessoaService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PessoaServiceImpl implements IPessoaService {

    private final IPessoaProducer pessoaProducer;
    private final IS3StorageService s3StorageService;
    private final  ObjectMapper mapper;
    private final Validator validator;

    /**
     * Método responsável por enviar payload ao aws sqs e enviar arquivo ao S3 da aws.
     * @param pessoa
     * @param arquivo
     * @return PessoaDTO
     * @throws IOException
     */
    @Override
    public PessoaDTO save(String pessoa, MultipartFile arquivo) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PessoaDTO pessoaDTO = mapper.readValue(pessoa, PessoaDTO.class);
        var violations = validator.validate(pessoaDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        String s3Key = pessoaDTO.getCpf() + "/" + arquivo.getOriginalFilename();
        pessoaDTO.setId(UUID.randomUUID().toString());
        pessoaDTO.setS3Key(s3Key);
        pessoaDTO.setEventId(UUID.randomUUID().toString());
        pessoaDTO.setEventType(EventType.CREATE);
        pessoaDTO.setTimestamp(System.currentTimeMillis());

        s3StorageService.enviarToS3(pessoaDTO, arquivo);
        pessoaProducer.enviarToSQS(pessoaDTO);

        return pessoaDTO;
    }

    /**
     * Método responsável por buscar Pessoa e arquivo pelo cpf da Pessoa no DynamoDB e posteriormente o arquivo no S3.
     * @param cpf
     * @return PessoaDTO
     */
    @Override
    public PessoaDTO buscarPessoa(String cpf) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(cpf);
        String valorCpf = jsonNode.get("cpf").asText();
        PessoaDTO pessoaDTO = PessoaDTO.builder().nome("John Doe").cpf(valorCpf).build(); //Apenas para validar o cpf.
        var violations = validator.validate(pessoaDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return pessoaDTO;
    }
}
