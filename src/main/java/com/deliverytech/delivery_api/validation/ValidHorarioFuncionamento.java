package com.deliverytech.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ATIVIDADE 1.2: Anotação de validação customizada para Horário de Funcionamento.
 */
@Constraint(validatedBy = HorarioFuncionamentoValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHorarioFuncionamento {
    String message() default "Formato de horário inválido. Use HH:MM-HH:MM (ex: 09:00-18:00)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}