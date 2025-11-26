package com.deliverytech.delivery_api.dto;

import com.deliverytech.delivery_api.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para registro de um novo usuário na plataforma")
public class RegisterRequest {

    @Schema(description = "Nome completo do usuário", example = "Carlos Entregador")
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "Email para login (deve ser único)", example = "carlos@entrega.com")
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @Schema(description = "Senha de acesso (mínimo 6 caracteres)", example = "123456")
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Schema(description = "Perfil de acesso do usuário", example = "ENTREGADOR")
    @NotNull(message = "O perfil (role) é obrigatório")
    private Role role;

    @Schema(description = "ID do restaurante (obrigatório apenas se role for RESTAURANTE)", example = "1")
    private Long restauranteId;
}