package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * ATIVIDADE 1.2: Implementação do validador de Telefone.
 * Aceita formatos (11) 99999-8888, (11) 8888-7777, e variações sem parênteses ou traço.
 */
public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    // Regex que aceita (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX, com ou sem ( ), - ou espaço.
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\s*\\(?\\d{2}\\)?\\s*)?(\\d{4,5}[\\s-]?\\d{4})\\s*$"
    );

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null || telefone.isBlank()) {
            return false; // @NotBlank deve ser usada em conjunto se for obrigatório
        }

        // Remove caracteres não numéricos para verificar o tamanho
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            return false;
        }

        // Valida o formato geral
        return PHONE_PATTERN.matcher(telefone).matches();
    }
}