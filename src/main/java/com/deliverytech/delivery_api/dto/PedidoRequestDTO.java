package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "DTO para a criação de um novo pedido") // ATIVIDADE 2.3
public class PedidoRequestDTO {

    @Schema(description = "ID do cliente que está fazendo o pedido", example = "1") // ATIVIDADE 2.3
    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;

    @Schema(description = "ID do restaurante de onde o pedido está sendo feito", example = "1") // ATIVIDADE 2.3
    @NotNull(message = "O ID do restaurante é obrigatório")
    private Long restauranteId;

    @Schema(description = "Lista de itens (produtos e quantidades) do pedido") // ATIVIDADE 2.3
    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid // Valida os ItemPedidoDTO dentro da lista
    private List<ItemPedidoDTO> itens;

    @Schema(description = "Instruções especiais (ex: tirar cebola, ponto da carne)", example = "Tirar a cebola da pizza, por favor.") // ATIVIDADE 2.3
    private String observacoes;
}