package com.deliverytech.delivery_api.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery_api.dto.reports.RelatorioVendasRestaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository <Pedido, Long> {

    // Buscar pedidos por cliente ID
    List<Pedido> findByClienteId(Long clienteId);

    // Buscar por número do pedido
    Pedido findByNumeroPedido(String numeroPedido);

    //Buscar pedidos por restaurante ID
    List<Pedido> findByRestauranteIdOrderByDataPedidoDesc(Long restauranteId);

    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    // Buscar pedidos entre datas
    List<Pedido> findByDataPedidoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<Pedido> findByStatus(StatusPedido status);

    @Query("SELECT p.restaurante.nome as restauranteNome, SUM(p.valorTotal) as totalVendas " +
            "FROM Pedido p " +
            "GROUP BY p.restaurante.nome " +
            "ORDER BY totalVendas DESC")
    List<RelatorioVendasRestaurante> findTotalVendasPorRestaurante();

    List<Pedido> findByValorTotalGreaterThan(BigDecimal valor);

    List<Pedido> findByDataPedidoBetweenAndStatus(LocalDateTime dataInicio, LocalDateTime dataFim, String status);
}
