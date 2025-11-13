package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para criar ou atualizar um produto do cardápio") // ATIVIDADE 2.3
public class ProdutoRequestDTO {

    @Schema(description = "Nome do produto", example = "Pizza Margherita") // ATIVIDADE 2.3
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "Descrição detalhada dos ingredientes", example = "Molho de tomate fresco, mussarela de búfala e manjericão") // ATIVIDADE 2.3
    private String descricao;

    @Schema(description = "Preço de venda do produto", example = "45.50") // ATIVIDADE 2.3
    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @Schema(description = "Categoria do produto no cardápio", example = "Pizzas Tradicionais") // ATIVIDADE 2.3
    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1") // ATIVIDADE 2.3
    @NotNull(message = "O ID do restaurante é obrigatório")
    private Long restauranteId;
}