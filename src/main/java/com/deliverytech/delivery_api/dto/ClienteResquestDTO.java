package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para criar ou atualizar um cliente") // ATIVIDADE 2.3
public class ClienteResquestDTO {

    @Schema(description = "Nome completo do cliente", example = "João da Silva") // ATIVIDADE 2.3
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Schema(description = "Email único do cliente (será usado para login)", example = "joao.silva@email.com") // ATIVIDADE 2.3
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    private String email;

    @Schema(description = "Telefone do cliente (com DDD)", example = "(11) 99999-8888") // ATIVIDADE 2.3
    @NotBlank(message = "O telefone é obrigatório")
    @Size(min = 10, max = 15, message = "O telefone deve ter entre 10 e 15 caracteres (com DDD)")
    private String telefone;

    @Schema(description = "Endereço principal de entrega", example = "Rua das Flores, 123, São Paulo/SP") // ATIVIDADE 2.3
    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

}