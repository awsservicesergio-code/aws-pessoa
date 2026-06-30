package com.pessoa.aws.s3.service.Impl;

import com.pessoa.aws.s3.config.S3Properties;
import com.pessoa.aws.s3.service.IS3StorageService;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.error.exceptions.SqsErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;


@Service
@RequiredArgsConstructor
public class S3StorageService implements IS3StorageService {

    private final Environment environment;

    private final S3Client s3Client;
    private final S3Properties properties;
    private final S3Presigner presigner;
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

    /**
     * Método responsável por direcionar a chamada do profile do s3 pre-signer.
     * @param key
     * @return String
     */
    @Override
    public String chamarUrlDownload(String key) {
        if (environment.matchesProfiles("local")) {
            return gerarUrlLocalDownload(key);
        } else {
            return gerarUrlAWSDownload(key);
        }
    }

    /**
     * Método responsável pela chamada do profile local do s3 pre-signer.
     * @param key
     * @return String
     */
    private String gerarUrlLocalDownload(String key) {
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket("pessoa-s3-local")
                        .key(key)
                        .responseContentDisposition(
                                "attachment; filename=\"" + extractFileName(key) + "\""
                        )
                        .build();
        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                        .build();
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Método responsável pela chamada do profile aws do s3 pre-signer.
     * @param key
     * @return String
     */
    private String gerarUrlAWSDownload(String key) {
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket("pessoa-s3-aws")
                        .key(key)
                        .responseContentDisposition(
                                "attachment; filename=\"" + extractFileName(key) + "\""
                        )
                        .build();
        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(getObjectRequest)
                        .build();
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Método reponsável por extrair o nome do arquivo.
     * @param key
     * @return String
     */
    private String extractFileName(String key) {
        return Paths.get(key).getFileName().toString();
    }
}
