package com.deliverytech.delivery_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
    List<Pedido> findByDataPedidoBetween(String dataInicio, String dataFim);
}
