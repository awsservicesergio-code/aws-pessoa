package com.pessoa.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {

    /**
     * Método responsável por validar o CPF.
     * @param cpf object to validate
     * @param context context in which the constraint is evaluated
     * @return boolean
     */
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {

        if (cpf == null) return false;
        // remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) return false;
        // evita CPFs com todos dígitos iguais
        if (cpf.chars().distinct().count() == 1) return false;
        try {
            int soma = 0;
            int peso = 10;

            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * peso--;
            }

            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;

            soma = 0;
            peso = 11;

            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * peso--;
            }

            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;

            return (cpf.charAt(9) - '0' == digito1) &&
                    (cpf.charAt(10) - '0' == digito2);

        } catch (Exception e) {
            return false;
        }
    }

}
