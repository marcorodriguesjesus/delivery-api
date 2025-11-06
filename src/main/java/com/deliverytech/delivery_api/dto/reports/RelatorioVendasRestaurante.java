package com.deliverytech.delivery_api.dto.reports;

import java.math.BigDecimal;

// Esta interface usa o "Spring Data Projections"
public interface RelatorioVendasRestaurante {
    String getRestauranteNome();
    BigDecimal getTotalVendas();
}