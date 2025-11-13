package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para criar ou atualizar um restaurante") // ATIVIDADE 2.3
public class RestauranteRequestDTO {

    @Schema(description = "Nome oficial do restaurante", example = "Pizzaria Bella Italia") // ATIVIDADE 2.3
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @Schema(description = "Tipo de culinária principal", example = "Italiana") // ATIVIDADE 2.3
    @NotBlank(message = "A categoria é obrigatória")
    private String categoria;

    @Schema(description = "Endereço físico do restaurante", example = "Av. Paulista, 1000, São Paulo/SP") // ATIVIDADE 2.3
    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;

    @Schema(description = "Telefone de contato do restaurante", example = "(11) 3333-4444") // ATIVIDADE 2.3
    private String telefone;

    @Schema(description = "Valor cobrado pela entrega", example = "5.99") // ATIVIDADE 2.3
    @NotNull(message = "A taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", message = "A taxa de entrega não pode ser negativa")
    private BigDecimal taxaEntrega;
}