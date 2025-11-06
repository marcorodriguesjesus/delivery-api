package com.deliverytech.delivery_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteResquetDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

}
