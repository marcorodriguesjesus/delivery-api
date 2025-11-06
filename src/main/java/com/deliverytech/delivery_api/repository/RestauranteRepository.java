package com.deliverytech.delivery_api.repository;

import java.util.List;
import java.util.Optional;

import com.deliverytech.delivery_api.dto.reports.RelatorioFaturamentoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.deliverytech.delivery_api.entity.Restaurante;

@Repository
public interface RestauranteRepository extends JpaRepository <Restaurante, Long>{
    // Buscar por nome
    Optional<Restaurante> findByNome(String nome);

    // Buscar restaurantes ativos
    List<Restaurante> findByAtivoTrue();

    // Buscar por categoria
    List<Restaurante> findByCategoria(String categoria);

    // Buscar por taxa menor ou igual a X
    List<Restaurante> findByTaxaEntregaLessThanEqual(Double taxaEntrega);

    //buscar por top 5 pedidos pelo nome do restaurante
    List<Restaurante> findTop5ByOrderByNomeAsc();

    @Query(value = "SELECT r.categoria, SUM(p.valor_total) as faturamentoTotal " +
            "FROM restaurantes r " +
            "JOIN pedidos p ON r.id = p.restaurante_id " +
            "GROUP BY r.categoria " +
            "ORDER BY faturamentoTotal DESC",
            nativeQuery = true)
    List<RelatorioFaturamentoCategoria> findFaturamentoPorCategoria();
}
