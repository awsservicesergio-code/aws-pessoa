package com.pessoa.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pessoa.aws.s3.service.IS3StorageService;
import com.pessoa.aws.sqs.IPessoaProducer;
import com.pessoa.dto.PessoaDTO;
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
    private final Validator validator;

    @Override
    public PessoaDTO save(String pessoa, MultipartFile arquivo) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        PessoaDTO pessoaDTO = mapper.readValue(pessoa, PessoaDTO.class);
        var violations = validator.validate(pessoaDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        pessoaDTO.setId(UUID.randomUUID().toString());
        pessoaProducer.enviarToSQS(pessoaDTO);
        s3StorageService.enviarToS3(pessoaDTO, arquivo);
        return pessoaDTO;
    }
}
