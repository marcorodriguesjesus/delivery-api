package com.deliverytech.delivery_api.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurantes")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String categoria;

    private String endereco;

    private String telefone;

    @Column(name = "taxa_entrega")
    private BigDecimal taxaEntrega;

    private BigDecimal avaliacao;

    private Boolean ativo;

    public void inativar() {
        this.ativo = false;
    }
}
