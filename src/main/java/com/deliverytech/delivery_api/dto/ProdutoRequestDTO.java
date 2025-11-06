package com.deliverytech.delivery_api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    private BigDecimal preco;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    @NotNull(message = "O ID do restaurante é obrigatório")
    private Long restauranteId;
}