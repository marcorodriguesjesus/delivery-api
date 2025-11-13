package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO de resposta com os dados de um produto") // ATIVIDADE 2.3
public class ProdutoResponseDTO {

    @Schema(description = "ID único do produto", example = "101") // ATIVIDADE 2.3
    private Long id;

    @Schema(description = "Nome do produto", example = "Pizza Margherita") // ATIVIDADE 2.3
    private String nome;

    @Schema(description = "Descrição detalhada dos ingredientes", example = "Molho de tomate fresco, mussarela de búfala e manjericão") // ATIVIDADE 2.3
    private String descricao;

    @Schema(description = "Preço de venda do produto", example = "45.50") // ATIVIDADE 2.3
    private BigDecimal preco;

    @Schema(description = "Categoria do produto no cardápio", example = "Pizzas Tradicionais") // ATIVIDADE 2.3
    private String categoria;

    @Schema(description = "Indica se o produto está disponível para venda", example = "true") // ATIVIDADE 2.3
    private Boolean disponivel;

    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1") // ATIVIDADE 2.3
    private Long restauranteId;
}