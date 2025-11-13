package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ATIVIDADE 3.2: DTO auxiliar para detalhar erros de validação (Bônus).
 */
@Schema(description = "Detalhe de um erro de validação de campo")
public record FieldValidationError(
        @Schema(description = "O campo que falhou na validação", example = "email")
        String field,

        @Schema(description = "A mensagem de erro", example = "O email deve ser válido")
        String message
) {
}