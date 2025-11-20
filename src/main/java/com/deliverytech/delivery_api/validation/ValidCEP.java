package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ATIVIDADE 1.2: Anotação de validação customizada para CEP.
 */
@Constraint(validatedBy = CEPValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCEP {
    String message() default "Formato de CEP inválido. Use XXXXX-XXX ou XXXXXXXX";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}