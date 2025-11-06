package com.deliverytech.delivery_api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {
    private Long id;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private String status;
    private BigDecimal valorTotal;
    private String observacoes;
    private Long clienteId;
    private Long restauranteId;
    private String itens; // JSON ou string representando os itens do pedido

}
