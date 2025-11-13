package com.deliverytech.delivery_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ATIVIDADE 3.2: DTO padronizado para respostas de erro.
 * Será aninhado dentro do ApiResponse.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Objeto de erro padronizado")
public class ApiError {

    @Schema(description = "O código de status HTTP", example = "404")
    private final int status;

    @Schema(description = "O tipo de erro", example = "NOT_FOUND")
    private final String error;

    @Schema(description = "Mensagem legível para o desenvolvedor", example = "Cliente não encontrado com ID: 99")
    private final String message;

    @Schema(description = "O path da API que foi chamado", example = "/api/clientes/99")
    private final String path;

    @Schema(description = "Detalhes específicos do erro (ex: erros de validação)")
    private Object details;

    public ApiError(HttpStatus status, String message, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }

    public void setDetails(Object details) {
        this.details = details;
    }
}