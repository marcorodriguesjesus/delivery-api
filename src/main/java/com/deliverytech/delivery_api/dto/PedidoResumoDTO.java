package com.deliverytech.delivery_api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO resumido para listagens
@Data
public class PedidoResumoDTO {
    private Long id;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private String status;
    private BigDecimal valorTotal;
    private String nomeRestaurante;
}