package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO resumido para listagens de pedidos (histórico)") // ATIVIDADE 2.3
public class PedidoResumoDTO {

    @Schema(description = "ID único do pedido", example = "501") // ATIVIDADE 2.3
    private Long id;

    @Schema(description = "Código único do pedido (visível para o cliente)", example = "PED-A8C4E") // ATIVIDADE 2.3
    private String numeroPedido;

    @Schema(description = "Data e hora em que o pedido foi criado") // ATIVIDADE 2.3
    private LocalDateTime dataPedido;

    @Schema(description = "Status atual do pedido", example = "ENTREGUE") // ATIVIDADE 2.3
    private String status;

    @Schema(description = "Valor total do pedido", example = "98.99") // ATIVIDADE 2.3
    private BigDecimal valorTotal;

    @Schema(description = "Nome do restaurante onde o pedido foi feito", example = "Pizzaria Bella Italia") // ATIVIDADE 2.3
    private String nomeRestaurante;
}