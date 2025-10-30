package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entities.Produto;
import com.deliverytech.delivery.entities.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome);

    List<Produto> findByRestaurante(Restaurante restaurante);

    List<Produto> findByRestauranteAndDisponivelTrue(Restaurante restaurante);

    List<Produto> findByCategoria(String categoria);

    List<Produto> findByCategoriaAndDisponivelTrue(String categoria);

    List<Produto> findByDisponivelTrue();

    List<Produto> findByDisponivelFalse();

    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :idRestaurante")
    List<Produto> findByIdRestaurante(@Param("idRestaurante") Long idRestaurante);

    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :idRestaurante AND p.disponivel = true")
    List<Produto> findDisponiveisByIdRestaurante(@Param("idRestaurante") Long idRestaurante);

    @Query("SELECT p FROM Produto p WHERE p.preco BETWEEN :precoMinimo AND :precoMaximo AND p.disponivel = true")
    List<Produto> findByFaixaPreco(@Param("precoMinimo") Double precoMinimo, @Param("precoMaximo") Double precoMaximo);
}