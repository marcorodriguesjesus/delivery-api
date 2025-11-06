package com.deliverytech.delivery_api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// DTO de resposta para um pedido completo
@Data
public class PedidoResponseDTO {
    private Long id;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private String status;
    private BigDecimal valorTotal;
    private String observacoes;


    // Usamos DTOs aninhados para a resposta
    private ClienteResponseDTO cliente;
    private RestauranteResponseDTO restaurante;

    // Você pode criar um "ItemPedidoResponseDTO" se precisar de mais detalhes
    private List<ItemPedidoDTO> itens;
}