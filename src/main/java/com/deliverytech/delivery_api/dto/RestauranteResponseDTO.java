package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO de resposta com os dados de um restaurante") // ATIVIDADE 2.3
public class RestauranteResponseDTO {

    @Schema(description = "ID único do restaurante", example = "1") // ATIVIDADE 2.3
    private Long id;

    @Schema(description = "Nome oficial do restaurante", example = "Pizzaria Bella Italia") // ATIVIDADE 2.3
    private String nome;

    @Schema(description = "Tipo de culinária principal", example = "Italiana") // ATIVIDADE 2.3
    private String categoria;

    @Schema(description = "Endereço físico do restaurante", example = "Av. Paulista, 1000, São Paulo/SP") // ATIVIDADE 2.3
    private String endereco;

    @Schema(description = "Telefone de contato do restaurante", example = "(11) 3333-4444") // ATIVIDADE 2.3
    private String telefone;

    @Schema(description = "Valor cobrado pela entrega", example = "5.99") // ATIVIDADE 2.3
    private BigDecimal taxaEntrega;

    @Schema(description = "Nota média de avaliação (0.0 a 5.0)", example = "4.7") // ATIVIDADE 2.3
    private BigDecimal avaliacao;

    @Schema(description = "Indica se o restaurante está aberto/ativo na plataforma", example = "true") // ATIVIDADE 2.3
    private Boolean ativo;
}