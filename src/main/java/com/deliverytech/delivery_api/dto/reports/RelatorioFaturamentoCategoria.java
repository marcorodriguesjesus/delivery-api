package com.deliverytech.delivery_api.dto.reports;

import java.math.BigDecimal;

public interface RelatorioFaturamentoCategoria {
    String getCategoria();
    BigDecimal getFaturamentoTotal();
}