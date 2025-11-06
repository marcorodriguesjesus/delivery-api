package com.deliverytech.delivery_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    @NotNull(message = "O ID do cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "O ID do restaurante é obrigatório")
    private Long restauranteId;

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid // Valida os ItemPedidoDTO dentro da lista
    private List<ItemPedidoDTO> itens;

    private String observacoes;
}