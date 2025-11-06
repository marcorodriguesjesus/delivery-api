package com.deliverytech.delivery_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clientes")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String telefone;

    private String endereco;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(nullable = true)
    private Boolean ativo;

    public void inativar() {
        this.ativo = false;
    }

}
