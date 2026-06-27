package com.pessoa.aws.s3.service;

import com.pessoa.aws.s3.config.S3Properties;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.error.exceptions.SqsErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.time.Duration;


@Service
@RequiredArgsConstructor
public class S3StorageService implements IS3StorageService {

    private final S3Client s3Client;
    private final S3Properties properties;
    private final StringRedisTemplate redisTemplate;

    /**
     * Método responsável pelo envio do arquivo para o aws s3.
     * @param pessoaDTO
     * @param file
     * @return String
     * @throws IOException
     */
    @Override
    public String enviarToS3(PessoaDTO pessoaDTO, MultipartFile file) throws IOException {

        String idempotencyKey = pessoaDTO.getCpf();
        String key = pessoaDTO.getCpf() + "/" + file.getOriginalFilename();
        Boolean isIdempotent = redisTemplate.opsForValue()
                .setIfAbsent(idempotencyKey, "PROCESSANDO", Duration.ofHours(1));

        if (Boolean.FALSE.equals(isIdempotent)) {
            throw new SqsErrorException("CPF já cadastrado.");
        }

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );
        } catch (Exception ex) {
            redisTemplate.delete(idempotencyKey);
            throw ex;
        }
        return idempotencyKey;
    }
}
