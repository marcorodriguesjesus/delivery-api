package com.deliverytech.delivery_api.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para o relatório de ranking de clientes")
public record RelatorioRankingClienteDTO(
        @Schema(example = "João Silva")
        String clienteNome,

        @Schema(example = "15")
        Long totalPedidos
) {
    // Construtor auxiliar para facilitar o mapeamento
    public RelatorioRankingClienteDTO(RelatorioRankingCliente projection) {
        this(projection.getClienteNome(), projection.getTotalPedidos());
    }
}