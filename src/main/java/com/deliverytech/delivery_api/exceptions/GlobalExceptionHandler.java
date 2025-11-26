package com.deliverytech.delivery_api.exceptions;

import com.deliverytech.delivery_api.dto.ApiError;
import com.deliverytech.delivery_api.dto.ApiResponse;
import com.deliverytech.delivery_api.dto.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Importante
import org.springframework.security.authorization.AuthorizationDeniedException; // Importante
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handler para Acesso Negado (403)
     * Captura exceções lançadas pelo @PreAuthorize ou outras regras de segurança.
     */
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            Exception ex, HttpServletRequest request) {

        ApiError error = new ApiError(
                HttpStatus.FORBIDDEN,
                "Acesso negado: Você não tem permissão para realizar esta ação.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(error));
    }

    /**
     * ATIVIDADE 3.1 e 3.2: Handler para Entidade Não Encontrada (404)
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
     * NOVO HANDLER (ATIVIDADE 2.2): Handler para Conflito de Dados (409)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflictException(
            ConflictException ex, HttpServletRequest request) {

        ApiError error = new ApiError(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(error));
    }

    /**
     * ATIVIDADE 3.1 e 3.2: Handler para Erros de Validação (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

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
        error.setDetails(validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(error));
    }

    /**
     * ATIVIDADE 3.1 e 3.2: Handler Genérico para Erros Inesperados (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        ex.printStackTrace(); // Log do erro no console para debug

        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado: " + ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(error));
    }
}