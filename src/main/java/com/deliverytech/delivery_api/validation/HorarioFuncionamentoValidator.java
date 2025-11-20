package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * ATIVIDADE 1.2: Implementação do validador de Horário de Funcionamento.
 */
public class HorarioFuncionamentoValidator implements ConstraintValidator<ValidHorarioFuncionamento, String> {

    // Regex para validar o formato HH:MM-HH:MM (formato 24h)
    private static final Pattern HORARIO_PATTERN = Pattern.compile(
            "^([01]\\d|2[0-3]):([0-5]\\d)-([01]\\d|2[0-3]):([0-5]\\d)$"
    );

    @Override
    public boolean isValid(String horario, ConstraintValidatorContext context) {
        if (horario == null || horario.isBlank()) {
            // Considera válido se for opcional. @NotBlank deve ser usado se for obrigatório.
            return true;
        }

        return HORARIO_PATTERN.matcher(horario).matches();
    }
}