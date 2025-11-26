package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credenciais para autenticação e obtenção do token")
public class LoginRequest {

    @Schema(description = "Email do usuário cadastrado", example = "admin@delivery.com")
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}