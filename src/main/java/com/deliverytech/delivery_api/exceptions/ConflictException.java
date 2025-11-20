package com.deliverytech.delivery_api.exceptions;

/**
 * ATIVIDADE 2.1: Exceção para conflitos de dados (ex: email duplicado).
 * Mapeada para o status HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}