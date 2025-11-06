package com.deliverytech.delivery_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.entity.Produto;

import java.util.List;


@Repository
public interface ProdutoRepository extends JpaRepository <Produto, Long> {
    // buscar produto por restaurante ID
     List<Produto> findByRestauranteId(Long restauranteId);

     // buscar por disponibilidade
     List<Produto> findByDisponivelTrue();

     // buscar por categoria
     List<Produto> findByCategoria(String categoria);

     // buscar por preço menor ou igual a X
     List<Produto> findByPrecoLessThanEqual(Double preco);
    
}
