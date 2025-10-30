package com.deliverytech.delivery.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_pedido")
    private String numeroPedido;

    @Column(name = "data_pedido")
    private LocalDateTime dataPedido;

    private String status;

    @Column(name = "valor_total")
    private Double valorTotal;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;

    private String itens;

    public boolean estaPendente() {
        return "PENDENTE".equalsIgnoreCase(status);
    }

    public boolean estaConfirmado() {
        return "CONFIRMADO".equalsIgnoreCase(status);
    }

    public boolean estaEntregue() {
        return "ENTREGUE".equalsIgnoreCase(status);
    }

    public boolean estaCancelado() {
        return "CANCELADO".equalsIgnoreCase(status);
    }

    public void confirmar() {
        if (!estaPendente()) {
            throw new IllegalStateException("Apenas pedidos pendentes podem ser confirmados");
        }
        this.status = "CONFIRMADO";
    }

    public void entregar() {
        if (!estaConfirmado()) {
            throw new IllegalStateException("Apenas pedidos confirmados podem ser entregues");
        }
        this.status = "ENTREGUE";
    }

    public void cancelar() {
        if (estaEntregue()) {
            throw new IllegalStateException("Pedidos entregues não podem ser cancelados");
        }
        this.status = "CANCELADO";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public String getItens() {
        return itens;
    }

    public void setItens(String itens) {
        this.itens = itens;
    }
}