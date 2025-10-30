package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entities.Cliente;
import com.deliverytech.delivery.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCliente(Cliente cliente);

    List<Pedido> findByStatus(String status);

    List<Pedido> findByClienteAndStatus(Cliente cliente, String status);

    @Query("SELECT p FROM Pedido p WHERE p.dataPedido BETWEEN :dataInicio AND :dataFim")
    List<Pedido> findByDataPedidoBetween(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT p FROM Pedido p WHERE p.cliente = :cliente AND p.dataPedido BETWEEN :dataInicio AND :dataFim")
    List<Pedido> findByClienteAndDataPedidoBetween(@Param("cliente") Cliente cliente,
                                                   @Param("dataInicio") LocalDateTime dataInicio,
                                                   @Param("dataFim") LocalDateTime dataFim);
}