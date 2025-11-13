package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ApiResponse;
import com.deliverytech.delivery_api.dto.PagedResponse;
import com.deliverytech.delivery_api.dto.PedidoResumoDTO;
import com.deliverytech.delivery_api.dto.reports.RelatorioProdutoVendidoDTO;
import com.deliverytech.delivery_api.dto.reports.RelatorioRankingClienteDTO;
import com.deliverytech.delivery_api.dto.reports.RelatorioVendasRestauranteDTO;
import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.services.PedidoService; // 1. IMPORTAR PedidoService
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private PedidoService pedidoService; // 2. INJETAR PedidoService

    @GetMapping("/vendas-por-restaurante")
    @Operation(summary = "Relatório de total de vendas agrupado por restaurante")
    public ResponseEntity<ApiResponse<List<RelatorioVendasRestauranteDTO>>> getVendasRestaurante() {

        List<RelatorioVendasRestauranteDTO> relatorio = pedidoRepository.findTotalVendasPorRestaurante()
                .stream()
                .map(RelatorioVendasRestauranteDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(relatorio));
    }

    @GetMapping("/clientes-mais-ativos")
    @Operation(summary = "Ranking de clientes por número total de pedidos")
    public ResponseEntity<ApiResponse<List<RelatorioRankingClienteDTO>>> getClientesMaisAtivos() {

        List<RelatorioRankingClienteDTO> relatorio = clienteRepository.findRankingClientesPorPedidos()
                .stream()
                .map(RelatorioRankingClienteDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(relatorio));
    }

    @GetMapping("/produtos-mais-vendidos")
    @Operation(summary = "Ranking dos produtos mais vendidos (Top N)")
    public ResponseEntity<ApiResponse<List<RelatorioProdutoVendidoDTO>>> getProdutosMaisVendidos(
            @Parameter(description = "Quantidade de produtos no ranking", example = "10")
            @RequestParam(defaultValue = "10") int limite) {

        List<RelatorioProdutoVendidoDTO> relatorio = produtoRepository.findProdutosMaisVendidos(limite)
                .stream()
                .map(RelatorioProdutoVendidoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(relatorio));
    }

    /**
     * ATIVIDADE 3.4: Corrigido para usar o PedidoService e suportar paginação.
     */
    @GetMapping("/pedidos-por-periodo")
    @Operation(summary = "Listar todos os pedidos dentro de um período (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> getPedidosPorPeriodo(
            @Parameter(description = "Data/Hora inicial (formato ISO: YYYY-MM-DDTHH:MM:SS)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @Parameter(description = "Data/Hora final (formato ISO: YYYY-MM-DDTHH:MM:SS)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 10, sort = "dataPedido") Pageable pageable) {

        // 3. USAR O SERVICE, passando null para o status (pois este endpoint não filtra por status)
        Page<PedidoResumoDTO> page = pedidoService.listarPedidos(null, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * ATIVIDADE 3.4: Corrigido para usar um novo método no PedidoService e suportar paginação.
     */
    @GetMapping("/pedidos-por-valor")
    @Operation(summary = "Relatório de pedidos acima de um determinado valor (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> getPedidosAcimaDe(
            @Parameter(description = "Valor mínimo do pedido", example = "100.00")
            @RequestParam BigDecimal valor,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 10, sort = "valorTotal") Pageable pageable) {

        // 3. USAR O NOVO MÉTODO DO SERVICE
        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosAcimaDeValor(valor, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }
}