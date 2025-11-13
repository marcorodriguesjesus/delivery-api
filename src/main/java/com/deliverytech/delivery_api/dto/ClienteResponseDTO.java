package com.deliverytech.delivery_api.dto;

import com.deliverytech.delivery_api.entity.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "DTO de resposta com os dados de um cliente") // ATIVIDADE 2.3
public class ClienteResponseDTO {

    @Schema(description = "ID único do cliente", example = "1") // ATIVIDADE 2.3
    private Long id;

    @Schema(description = "Nome completo do cliente", example = "João da Silva") // ATIVIDADE 2.3
    private String nome;

    @Schema(description = "Email único do cliente", example = "joao.silva@email.com") // ATIVIDADE 2.3
    private String email;

    @Schema(description = "Telefone do cliente", example = "(11) 99999-8888") // ATIVIDADE 2.3
    private String telefone;

    @Schema(description = "Endereço principal de entrega", example = "Rua das Flores, 123, São Paulo/SP") // ATIVIDADE 2.3
    private String endereco;

    @Schema(description = "Indica se o cliente está ativo na plataforma", example = "true") // ATIVIDADE 2.3
    private Boolean ativo;

    public ClienteResponseDTO(Cliente save) {
        this.id = save.getId();
        this.nome = save.getNome();
        this.email = save.getEmail();
        this.telefone = save.getTelefone();
        this.endereco = save.getEndereco();
        this.ativo = save.getAtivo();
    }
}