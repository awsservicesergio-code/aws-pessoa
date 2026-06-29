package com.pessoa.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.service.IPessoaConsultaService;
import com.pessoa.service.IPessoaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


@RestController
@RequestMapping(path = "/api/v1/pessoas")
@RequiredArgsConstructor
public class PessoaResource {

    private final IPessoaService pessoaService;
    private final IPessoaConsultaService pessoaConsultaService;

    /**
     * Método responsável por receber requisições de save do cadastro de pessoas.
     * @param pessoa
     * @param arquivo
     * @return ResponseEntity<PessoaDTO>
     * @throws IOException
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PessoaDTO> save(@RequestPart("pessoa") String pessoa,
                                          @RequestPart("arquivo") MultipartFile arquivo) throws IOException {
        PessoaDTO response = pessoaService.save(pessoa, arquivo);
        return ResponseEntity.status(HttpStatus.CREATED).header("Id", response.getId()).body(response);
    }

    /**
     * Método responsável por buscar Pessoa e arquivo pelo cpf da Pessoa no DynamoDB e posteriormente o arquivo no S3.
     * @param cpf
     * @return ResponseEntity<PessoaDTO>
     */
    @PostMapping(path = "/buscar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PessoaDTO> getPessoa(@RequestBody String cpf) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        return ResponseEntity.status(HttpStatus.OK).body(pessoaConsultaService.buscarPessoa(cpf));
    }
}
