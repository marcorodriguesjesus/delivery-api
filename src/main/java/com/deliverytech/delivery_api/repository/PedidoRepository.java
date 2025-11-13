package com.deliverytech.delivery_api.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery_api.dto.reports.RelatorioVendasRestaurante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository <Pedido, Long> {

    // Buscar pedidos por cliente ID
    Page<Pedido> findByClienteId(Long clienteId, Pageable pageable);

    // Buscar por número do pedido
    Pedido findByNumeroPedido(String numeroPedido);

    //Buscar pedidos por restaurante ID
    Page<Pedido> findByRestauranteIdOrderByDataPedidoDesc(Long restauranteId, Pageable pageable);

    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    // Buscar pedidos entre datas
    Page<Pedido> findByDataPedidoBetween(LocalDateTime dataInicio, LocalDateTime dataFim, Pageable pageable);

    Page<Pedido> findByStatus(String status, Pageable pageable); // Status é String na entidade

    @Query("SELECT p.restaurante.nome as restauranteNome, SUM(p.valorTotal) as totalVendas " +
            "FROM Pedido p " +
            "GROUP BY p.restaurante.nome " +
            "ORDER BY totalVendas DESC")
    List<RelatorioVendasRestaurante> findTotalVendasPorRestaurante();

    // ATIVIDADE 3.4: Corrigido para suportar paginação
    Page<Pedido> findByValorTotalGreaterThan(BigDecimal valor, Pageable pageable);

    List<Pedido> findByDataPedidoBetweenAndStatus(LocalDateTime dataInicio, LocalDateTime dataFim, String status);

    // ATIVIDADE 3.4: Corrigido para suportar paginação
    Page<Pedido> findByDataPedidoBetweenAndStatus(LocalDateTime dataInicio, LocalDateTime dataFim, String status, Pageable pageable);
}