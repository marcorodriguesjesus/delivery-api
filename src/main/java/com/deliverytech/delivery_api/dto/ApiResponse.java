package com.deliverytech.delivery_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ATIVIDADE 3.2: Wrapper padronizado para TODAS as respostas da API.
 * Garante que o frontend/mobile sempre receba um JSON com a mesma estrutura.
 *
 * @param <T> O tipo do dado de sucesso (pode ser um DTO, ou um PagedResponse)
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Não incluir campos nulos no JSON
@Schema(description = "Wrapper de resposta padronizado para toda a API")
public class ApiResponse<T> {

    @Schema(description = "Indica se a operação foi bem-sucedida", example = "true")
    private final boolean success;

    @Schema(description = "Timestamp da resposta")
    private final LocalDateTime timestamp;

    @Schema(description = "Os dados de resposta em caso de sucesso")
    private T data; // Dados de sucesso

    @Schema(description = "O objeto de erro em caso de falha")
    private ApiError error; // Dados de erro

    // Construtor para Sucesso
    private ApiResponse(T data) {
        this.success = true;
        this.timestamp = LocalDateTime.now();
        this.data = data;
        this.error = null;
    }

    // Construtor para Erro
    private ApiResponse(ApiError error) {
        this.success = false;
        this.timestamp = LocalDateTime.now();
        this.data = null;
        this.error = error;
    }

    /**
     * Método estático para facilitar a criação de respostas de sucesso
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    /**
     * Método estático para facilitar a criação de respostas de erro
     */
    public static <T> ApiResponse<T> error(ApiError error) {
        return new ApiResponse<>(error);
    }
}