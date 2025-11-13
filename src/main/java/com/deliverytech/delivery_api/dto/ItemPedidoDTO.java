package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Representa um item dentro de um pedido") // ATIVIDADE 2.3
public class ItemPedidoDTO {

    @Schema(description = "ID do produto que está sendo pedido", example = "101") // ATIVIDADE 2.3
    @NotNull(message = "O ID do produto é obrigatório")
    private Long produtoId;

    @Schema(description = "Quantidade desejada do produto", example = "2") // ATIVIDADE 2.3
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser de pelo menos 1")
    private Integer quantidade;
}