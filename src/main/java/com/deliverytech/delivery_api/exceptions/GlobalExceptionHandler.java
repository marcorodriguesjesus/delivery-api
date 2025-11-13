package com.deliverytech.delivery_api.exceptions;

import com.deliverytech.delivery_api.dto.ApiError;
import com.deliverytech.delivery_api.dto.ApiResponse;
import com.deliverytech.delivery_api.dto.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Importações de java.time, java.util.Map e java.util.HashMap não são mais necessárias
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ATIVIDADE 3.1 e 3.2: Handler para Entidade Não Encontrada (404)
     * Agora retorna o envelope ApiResponse padronizado.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(error));
    }

    /**
     * ATIVIDADE 3.1 e 3.2: Handler para Regra de Negócio (400)
     * Agora retorna o envelope ApiResponse padronizado.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    /**
     * ATIVIDADE 3.1 e 3.2: Handler para Erros de Validação (400)
     * Agora retorna o envelope ApiResponse com detalhes dos campos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        // Converte os erros de campo para o nosso DTO auxiliar
        List<FieldValidationError> validationErrors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> new FieldValidationError(
                        ((FieldError) error).getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Erro de validação. Verifique os campos.",
                request.getRequestURI()
        );
        error.setDetails(validationErrors); // Anexa os detalhes

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    /**
     * ATIVIDADE 3.1 e 3.2: Handler Genérico para Erros Inesperados (500)
     * Agora retorna o envelope ApiResponse padronizado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        // Logar o stack trace real no console do servidor (importante para debug)
        ex.printStackTrace();

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado: " + ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(error));
    }

    // O DTO ValidationErrorResponse não é mais necessário, pois foi substituído por ApiError
}