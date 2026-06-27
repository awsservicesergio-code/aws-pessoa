package com.pessoa.resource;

import com.pessoa.dto.PessoaDTO;
import com.pessoa.service.IPessoaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@RestController
@RequestMapping(path = "/api/v1/pessoas")
@RequiredArgsConstructor
public class PessoaResource {

    private final IPessoaService pessoaService;

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
}
