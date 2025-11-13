package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "DTO de resposta para um pedido completo (com dados aninhados)") // ATIVIDADE 2.3
public class PedidoResponseDTO {

    @Schema(description = "ID único do pedido", example = "501") // ATIVIDADE 2.3
    private Long id;

    @Schema(description = "Código único do pedido (visível para o cliente)", example = "PED-A8C4E") // ATIVIDADE 2.3
    private String numeroPedido;

    @Schema(description = "Data e hora em que o pedido foi criado") // ATIVIDADE 2.3
    private LocalDateTime dataPedido;

    @Schema(description = "Status atual do pedido", example = "PENDENTE") // ATIVIDADE 2.3
    private String status;

    @Schema(description = "Valor total (subtotal dos itens + taxa de entrega)", example = "98.99") // ATIVIDADE 2.3
    private BigDecimal valorTotal;

    @Schema(description = "Instruções especiais do cliente", example = "Tirar a cebola da pizza, por favor.") // ATIVIDADE 2.3
    private String observacoes;

    @Schema(description = "Dados do cliente que fez o pedido") // ATIVIDADE 2.3
    private ClienteResponseDTO cliente;

    @Schema(description = "Dados do restaurante que recebeu o pedido") // ATIVIDADE 2.3
    private RestauranteResponseDTO restaurante;

    @Schema(description = "Lista de itens que compõem o pedido") // ATIVIDADE 2.3
    private List<ItemPedidoDTO> itens;
}