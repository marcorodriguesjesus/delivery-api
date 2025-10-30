package com.deliverytech.delivery.repository;

import com.deliverytech.delivery.entities.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    List<Restaurante> findByAtivoTrue();

    boolean existsByTelefone(String telefone);

    List<Restaurante> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    List<Restaurante> findByCategoria(String categoria);

    @Query("SELECT r FROM Restaurante r WHERE r.avaliacao BETWEEN :minimo AND :maximo")
    List<Restaurante> findByAvaliacaoBetween(@Param("minimo") Double minimo, @Param("maximo") Double maximo);
}