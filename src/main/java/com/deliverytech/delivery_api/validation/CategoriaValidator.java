package com.deliverytech.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ATIVIDADE 1.2: Implementação do validador de Categoria.
 * Verifica se o valor recebido está na lista de 'acceptedValues' da anotação.
 */
public class CategoriaValidator implements ConstraintValidator<ValidCategoria, String> {

    private Set<String> acceptedValues;
    private String acceptedValuesString;

    @Override
    public void initialize(ValidCategoria constraintAnnotation) {
        // Carrega os valores permitidos da anotação para um Set (para performance)
        this.acceptedValues = new HashSet<>(Arrays.asList(constraintAnnotation.acceptedValues()));
        this.acceptedValuesString = Arrays.stream(constraintAnnotation.acceptedValues())
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean isValid(String categoria, ConstraintValidatorContext context) {
        if (categoria == null || categoria.isBlank()) {
            // Considera válido se for opcional. @NotBlank deve ser usado se for obrigatório.
            return true;
        }

        // Substitui a variável {acceptedValues} na mensagem de erro
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate()
                        .replace("{acceptedValues}", this.acceptedValuesString)
        ).addConstraintViolation();

        // Verifica se a categoria recebida está no set de valores permitidos
        return acceptedValues.contains(categoria);
    }
}