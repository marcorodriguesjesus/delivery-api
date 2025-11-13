package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.dto.reports.RelatorioProdutoVendido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.entity.Produto;

import java.util.List;


@Repository
public interface ProdutoRepository extends JpaRepository <Produto, Long> {
    // buscar produto por restaurante ID
    List<Produto> findByRestauranteId(Long restauranteId);

    // buscar por disponibilidade
    List<Produto> findByDisponivelTrue();

    /**
     * ATIVIDADE 3.4: Modificado para suportar paginação
     */
    Page<Produto> findByCategoria(String categoria, Pageable pageable);

    // buscar por preço menor ou igual a X
    List<Produto> findByPrecoLessThanEqual(Double preco);

    @Query(value = "SELECT pr.nome as produtoNome, COUNT(p.id) as quantidadeVendida " +
            "FROM produtos pr " +
            "JOIN pedidos p ON p.itens LIKE CONCAT('%', pr.nome, '%') " +
            "GROUP BY pr.nome " +
            "ORDER BY quantidadeVendida DESC " +
            "LIMIT :limite",
            nativeQuery = true)
    List<RelatorioProdutoVendido> findProdutosMaisVendidos(@Param("limite") int limite);

    /**
     * ATIVIDADE 3.4: Modificado para suportar paginação
     */
    Page<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId, Pageable pageable);

    /**
     * ATIVIDADE 3.4: Modificado para suportar paginação
     */
    Page<Produto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}