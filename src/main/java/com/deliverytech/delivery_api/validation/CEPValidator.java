package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ATIVIDADE 1.2: Implementação do validador de CEP.
 * Remove todos os caracteres não numéricos e verifica se o resultado tem 8 dígitos.
 */
public class CEPValidator implements ConstraintValidator<ValidCEP, String> {

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null || cep.isBlank()) {
            // Considera válido se for opcional. @NotBlank deve ser usado em conjunto se for obrigatório.
            return true;
        }

        // Remove traços, pontos, etc.
        String digitos = cep.replaceAll("\\D", "");

        // CEP no Brasil deve ter 8 dígitos.
        return digitos.length() == 8;
    }
}