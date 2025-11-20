package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ATIVIDADE 1.2: Anotação de validação customizada para Categoria.
 * Permite definir uma lista de valores aceitos.
 */
@Constraint(validatedBy = CategoriaValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategoria {

    // Mensagem de erro padrão. {acceptedValues} será substituído
    String message() default "Categoria inválida. Os valores permitidos são: {acceptedValues}";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * @return Os valores de categoria aceitos
     */
    String[] acceptedValues();
}