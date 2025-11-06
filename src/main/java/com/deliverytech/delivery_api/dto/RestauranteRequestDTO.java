package com.deliverytech.delivery_api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RestauranteRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

    private String telefone;

    @NotNull(message = "A taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", message = "A taxa de entrega não pode ser negativa")
    private BigDecimal taxaEntrega;
}