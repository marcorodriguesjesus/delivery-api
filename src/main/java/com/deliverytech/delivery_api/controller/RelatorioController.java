package com.deliverytech.delivery_api.controller;

// 1. IMPORTAR OS NOVOS DTOs
import com.deliverytech.delivery_api.dto.reports.RelatorioProdutoVendidoDTO;
import com.deliverytech.delivery_api.dto.reports.RelatorioRankingClienteDTO;
import com.deliverytech.delivery_api.dto.reports.RelatorioVendasRestauranteDTO;
// (imports das interfaces antigas não são mais necessários aqui)
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // 2. IMPORTAR COLLECTORS

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
@Tag(name = "Relatórios", description = "Endpoints para extração de dados e métricas de negócio")
public class RelatorioController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/vendas-por-restaurante")
    @Operation(summary = "Relatório de total de vendas agrupado por restaurante")
    // 3. MUDAR O TIPO DE RETORNO (de ? para o DTO concreto)
    public ResponseEntity<List<RelatorioVendasRestauranteDTO>> getVendasRestaurante() {
        // 4. MAPEAR A PROJEÇÃO (Interface) PARA O DTO (Record/Classe)
        List<RelatorioVendasRestauranteDTO> relatorio = pedidoRepository.findTotalVendasPorRestaurante()
                .stream()
                .map(RelatorioVendasRestauranteDTO::new) // Usa o construtor auxiliar
                .collect(Collectors.toList());
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Endpoint RENOMEADO (ATIVIDADE 1.4)
     */
    @GetMapping("/clientes-mais-ativos")
    @Operation(summary = "Ranking de clientes por número total de pedidos")
    // 3. MUDAR O TIPO DE RETORNO
    public ResponseEntity<List<RelatorioRankingClienteDTO>> getClientesMaisAtivos() {
        // 4. MAPEAR A PROJEÇÃO (Interface) PARA O DTO (Record/Classe)
        List<RelatorioRankingClienteDTO> relatorio = clienteRepository.findRankingClientesPorPedidos()
                .stream()
                .map(RelatorioRankingClienteDTO::new) // Usa o construtor auxiliar
                .collect(Collectors.toList());
        return ResponseEntity.ok(relatorio);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.4)
     */
    @GetMapping("/produtos-mais-vendidos")
    @Operation(summary = "Ranking dos produtos mais vendidos (Top N)")
    // 3. MUDAR O TIPO DE RETORNO
    public ResponseEntity<List<RelatorioProdutoVendidoDTO>> getProdutosMaisVendidos(
            @Parameter(description = "Quantidade de produtos no ranking", example = "10")
            @RequestParam(defaultValue = "10") int limite) {

        // 4. MAPEAR A PROJEÇÃO (Interface) PARA O DTO (Record/Classe)
        List<RelatorioProdutoVendidoDTO> relatorio = produtoRepository.findProdutosMaisVendidos(limite)
                .stream()
                .map(RelatorioProdutoVendidoDTO::new) // Usa o construtor auxiliar
                .collect(Collectors.toList());
        return ResponseEntity.ok(relatorio);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.4)
     */
    @GetMapping("/pedidos-por-periodo")
    @Operation(summary = "Listar todos os pedidos dentro de um período (datas obrigatórias)")
    public ResponseEntity<List<Pedido>> getPedidosPorPeriodo(
            @Parameter(description = "Data/Hora inicial (formato ISO: YYYY-MM-DDTHH:MM:SS)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @Parameter(description = "Data/Hora final (formato ISO: YYYY-MM-DDTHH:MM:SS)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(pedidoRepository.findByDataPedidoBetween(dataInicio, dataFim));
    }

    // Endpoint antigo (exemplo)
    @GetMapping("/pedidos-por-valor")
    @Operation(summary = "Relatório de pedidos acima de um determinado valor (Exemplo)")
    // 5. CORRIGIR O '?' PARA O TIPO CORRETO (List<Pedido>)
    public ResponseEntity<List<Pedido>> getPedidosAcimaDe(@RequestParam BigDecimal valor) {
        return ResponseEntity.ok(pedidoRepository.findByValorTotalGreaterThan(valor));
    }
}