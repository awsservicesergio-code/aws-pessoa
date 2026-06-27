package com.pessoa.aws.s3.service;

import com.pessoa.dto.PessoaDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface IS3StorageService {
     String enviarToS3(PessoaDTO pessoaDTO, MultipartFile file) throws IOException;
}
