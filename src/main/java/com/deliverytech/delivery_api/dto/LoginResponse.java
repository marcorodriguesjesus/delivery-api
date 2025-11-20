package com.deliverytech.delivery_api.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long expiresIn; // Tempo em milissegundos ou data de expiração
    private UserResponse user;

    public LoginResponse(String token, Long expiresIn, UserResponse user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}