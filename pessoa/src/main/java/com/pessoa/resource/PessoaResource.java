package com.pessoa.resource;

import com.pessoa.dto.PessoaDTO;
import com.pessoa.service.IPessoaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/pessoas")
@RequiredArgsConstructor
public class PessoaResource {

    private final IPessoaService pessoaService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PessoaDTO>> save(@RequestBody @Valid List<@Valid PessoaDTO> pessoaDTO){
        List<PessoaDTO> response = pessoaService.save(pessoaDTO);
        List<String> ids = response.stream().map(PessoaDTO::getId).toList();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Ids", String.join(" , ", ids));
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(response);
    }
}
