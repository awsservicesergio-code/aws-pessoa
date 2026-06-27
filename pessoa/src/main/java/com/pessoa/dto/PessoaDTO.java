package com.pessoa.dto;

import com.pessoa.config.CPF;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {
    private String id;
    @NotBlank(message = "Nome não pode ser nulo ou vazio")
    private String nome;
    @CPF
    @NotBlank(message = "CPF não pode ser nulo ou vazio")
    private String cpf;
    private String s3Key;
}
