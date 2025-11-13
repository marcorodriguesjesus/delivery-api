package com.deliverytech.delivery_api.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "DTO para o relatório de vendas por restaurante")
public record RelatorioVendasRestauranteDTO(
        @Schema(example = "Pizzaria Bella")
        String restauranteNome,

        @Schema(example = "4500.75")
        BigDecimal totalVendas
) {
    // Construtor auxiliar para facilitar o mapeamento
    public RelatorioVendasRestauranteDTO(RelatorioVendasRestaurante projection) {
        this(projection.getRestauranteNome(), projection.getTotalVendas());
    }
}