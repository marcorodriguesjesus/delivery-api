package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ATIVIDADE 1.2: Anotação de validação customizada para Telefone.
 */
@Constraint(validatedBy = TelefoneValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefone {
    String message() default "Formato de telefone inválido. Use (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}