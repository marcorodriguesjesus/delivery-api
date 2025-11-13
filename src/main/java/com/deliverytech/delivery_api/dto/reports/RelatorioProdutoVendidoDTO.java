package com.deliverytech.delivery_api.dto.reports;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para o relatório de produtos mais vendidos")
public record RelatorioProdutoVendidoDTO(
        @Schema(example = "Pizza Calabresa")
        String produtoNome,

        @Schema(example = "88")
        Long quantidadeVendida
) {
    // Construtor auxiliar para facilitar o mapeamento
    public RelatorioProdutoVendidoDTO(RelatorioProdutoVendido projection) {
        this(projection.getProdutoNome(), projection.getQuantidadeVendida());
    }
}