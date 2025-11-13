package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.dto.reports.RelatorioRankingCliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.deliverytech.delivery_api.entity.Cliente;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ClienteRepository extends JpaRepository <Cliente, Long> {

    // Buscar cliente por email (método derivado)
    Optional<Cliente> findByEmail(String email);

    // Verificar se email já existe
    boolean existsByEmail(String email);

    /**
     * ATIVIDADE 3.4: Modificado para suportar paginação
     * @param pageable Parâmetros de paginação (page, size, sort)
     * @return Uma página (Page) de clientes ativos
     */
    Page<Cliente> findByAtivoTrue(Pageable pageable);

    // Buscar clientes por nome (contendo)
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    @Query(value = "SELECT c.nome as clienteNome, COUNT(p.id) as totalPedidos " +
            "FROM clientes c " +
            "JOIN pedidos p ON c.id = p.cliente_id " +
            "GROUP BY c.nome " +
            "ORDER BY totalPedidos DESC",
            nativeQuery = true)
    List<RelatorioRankingCliente> findRankingClientesPorPedidos();

}