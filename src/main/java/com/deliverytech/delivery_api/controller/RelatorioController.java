package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.reports.RelatorioProdutoVendido;
import com.deliverytech.delivery_api.dto.reports.RelatorioRankingCliente;
import com.deliverytech.delivery_api.dto.reports.RelatorioVendasRestaurante;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository; // IMPORTADO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // IMPORTADO
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime; // IMPORTADO
import java.util.List; // IMPORTADO

@RestController
@RequestMapping("/api/relatorios") // Caminho base /api/relatorios
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository; // ADICIONADO

    @GetMapping("/vendas-por-restaurante")
    public ResponseEntity<?> getVendasRestaurante() {
        return ResponseEntity.ok(pedidoRepository.findTotalVendasPorRestaurante());
    }

    /**
     * Endpoint RENOMEADO (ATIVIDADE 1.4)
     */
    @GetMapping("/clientes-mais-ativos")
    public ResponseEntity<List<RelatorioRankingCliente>> getClientesMaisAtivos() {
        return ResponseEntity.ok(clienteRepository.findRankingClientesPorPedidos());
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.4)
     */
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<RelatorioProdutoVendido>> getProdutosMaisVendidos(
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(produtoRepository.findProdutosMaisVendidos(limite));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.4)
     */
    @GetMapping("/pedidos-por-periodo")
    public ResponseEntity<List<Pedido>> getPedidosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(pedidoRepository.findByDataPedidoBetween(dataInicio, dataFim));
    }

    // Endpoint antigo (exemplo)
    @GetMapping("/pedidos-por-valor")
    public ResponseEntity<?> getPedidosAcimaDe(@RequestParam BigDecimal valor) {
        return ResponseEntity.ok(pedidoRepository.findByValorTotalGreaterThan(valor));
    }
}