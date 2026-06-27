package com.pessoa.aws.s3.service;

import com.pessoa.aws.s3.config.S3Properties;
import com.pessoa.dto.PessoaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3StorageService implements IS3StorageService {

    private final S3Client s3Client;
    private final S3Properties properties;

    @Override
    public String enviarToS3(PessoaDTO pessoaDTO, MultipartFile file) throws IOException {
        String idempotencyKey = pessoaDTO.getCpf() + "/" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(idempotencyKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(
                        file.getInputStream(),
                        file.getSize()
                )
        );
        return idempotencyKey;
    }
}
