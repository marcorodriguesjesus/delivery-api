package com.deliverytech.delivery_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ValidationErrorResponse error = new ValidationErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Entidade não encontrada",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ValidationErrorResponse> handleBusinessException(BusinessException ex) {
        ValidationErrorResponse error = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de regra de negócio",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos",
                errors.toString(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGenericException(Exception ex) {
        ValidationErrorResponse error = new ValidationErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                "Ocorreu um erro inesperado",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
