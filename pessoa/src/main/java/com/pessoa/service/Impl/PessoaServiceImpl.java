package com.pessoa.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pessoa.aws.sqs.PessoaProducer;
import com.pessoa.dto.PessoaDTO;
import com.pessoa.error.exceptions.SqsErrorException;
import com.pessoa.service.IPessoaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PessoaServiceImpl implements IPessoaService {

    private final PessoaProducer pessoaProducer;

    @Override
    public List<PessoaDTO> save(List<PessoaDTO> pessoaDTO) {
        pessoaDTO.forEach(valor -> {valor.setId(UUID.randomUUID().toString());});
        pessoaDTO.forEach(pessoa -> {
            try {
                pessoaProducer.enviar(pessoa);
            } catch (JsonProcessingException e) {
                throw new SqsErrorException();
            }
        });
        return pessoaDTO;
    }
}
