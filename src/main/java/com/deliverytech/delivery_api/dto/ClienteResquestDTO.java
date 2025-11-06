package com.deliverytech.delivery_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // 1. Importar
import lombok.Data;

@Data
public class ClienteResquestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres") // 2. Adicionar @Size (Bônus)
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Size(min = 10, max = 15, message = "O telefone deve ter entre 10 e 15 caracteres (com DDD)") // 3. Adicionar @Size
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

}