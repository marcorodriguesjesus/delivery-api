package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para criar ou atualizar um produto do cardápio")
public class ProdutoRequestDTO {

    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres") // Adicionando Size
    private String nome;

    @Schema(description = "Descrição detalhada dos ingredientes", example = "Molho de tomate fresco, mussarela de búfala e manjericão")
    @Size(min = 10, message = "A descrição deve ter no mínimo 10 caracteres") // ATIVIDADE 1.1
    private String descricao;

    @Schema(description = "Preço de venda do produto", example = "45.50")
    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @DecimalMax(value = "500.00", message = "O preço não pode exceder R$ 500,00") // ATIVIDADE 1.1
    private BigDecimal preco;

    @Schema(description = "Categoria do produto no cardápio", example = "Pizzas Tradicionais")
    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    @Schema(description = "ID do restaurante ao qual o produto pertence", example = "1")
    @NotNull(message = "O ID do restaurante é obrigatório")
    private Long restauranteId;
}