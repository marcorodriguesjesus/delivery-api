package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // ... outros repositórios ...

    @GetMapping("/vendas-por-restaurante")
    public ResponseEntity<?> getVendasRestaurante() {
        return ResponseEntity.ok(pedidoRepository.findTotalVendasPorRestaurante());
    }

    @GetMapping("/ranking-clientes")
    public ResponseEntity<?> getRankingClientes() {
        return ResponseEntity.ok(clienteRepository.findRankingClientesPorPedidos());
    }

    @GetMapping("/pedidos-por-valor")
    public ResponseEntity<?> getPedidosAcimaDe(@RequestParam BigDecimal valor) {
        return ResponseEntity.ok(pedidoRepository.findByValorTotalGreaterThan(valor));
    }
}