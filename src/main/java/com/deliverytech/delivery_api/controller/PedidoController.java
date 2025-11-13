package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery_api.dto.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.dto.PedidoResumoDTO;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.services.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento do ciclo de vida dos pedidos") // ATIVIDADE 2.2
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private RestauranteRepository restauranteRepository;

    /**
     * 2.4: POST /api/pedidos - Criar pedido (transação complexa)
     */
    @PostMapping
    @Operation(summary = "Criar um novo pedido (transação complexa)") // ATIVIDADE 2.2
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO pedido = pedidoService.criarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.3): GET /api/pedidos - Listar com filtros
     */
    @GetMapping
    @Operation(summary = "Listar pedidos com filtros opcionais de status e período") // ATIVIDADE 2.2
    public ResponseEntity<List<PedidoResumoDTO>> listarPedidos(
            @Parameter(description = "Filtrar por status", example = "PENDENTE") // ATIVIDADE 2.2
            @RequestParam(required = false) StatusPedido status,

            @Parameter(description = "Data/Hora inicial (formato ISO: YYYY-MM-DDTHH:MM:SS)", example = "2025-10-01T00:00:00") // ATIVIDADE 2.2
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,

            @Parameter(description = "Data/Hora final (formato ISO: YYYY-MM-DDTHH:MM:SS)", example = "2025-10-31T23:59:59") // ATIVIDADE 2.2
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        List<PedidoResumoDTO> pedidos = pedidoService.listarPedidos(status, dataInicio, dataFim);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * 2.4: GET /api/pedidos/{id} - Buscar pedido completo
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um pedido completo pelo ID (com dados do cliente e restaurante)") // ATIVIDADE 2.2
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @Parameter(description = "ID do pedido", example = "501") @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorId(id));
    }

    /**
     * 2.4: PATCH /api/pedidos/{id}/status - Atualizar status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar o status de um pedido") // ATIVIDADE 2.2
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Parameter(description = "Novo status do pedido") @RequestParam StatusPedido status) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id, status);
        return ResponseEntity.ok(pedido);
    }

    /**
     * 2.4: DELETE /api/pedidos/{id} - Cancelar pedido
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar um pedido (se as regras de negócio permitirem)") // ATIVIDADE 2.2
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {
        PedidoResponseDTO pedidoCancelado = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(pedidoCancelado);
    }

    /**
     * 2.4: POST /api/pedidos/calcular - Calcular total sem salvar
     */
    @PostMapping("/calcular")
    @Operation(summary = "Calcular o total de um pedido (sem salvar no banco)") // ATIVIDADE 2.2
    public ResponseEntity<BigDecimal> calcularTotal(@Valid @RequestBody PedidoRequestDTO dto) {
        Restaurante r = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        BigDecimal total = pedidoService.calcularTotalPedido(dto.getItens(), dto.getRestauranteId(), r.getTaxaEntrega());
        return ResponseEntity.ok(total);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.3): GET /api/pedidos/cliente/{clienteId}
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Buscar o histórico de pedidos de um cliente") // ATIVIDADE 2.2
    public ResponseEntity<List<PedidoResumoDTO>> buscarPedidosPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        List<PedidoResumoDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.3): GET /api/pedidos/restaurante/{restauranteId}
     */
    @GetMapping("/restaurante/{restauranteId}")
    @Operation(summary = "Buscar os pedidos recebidos por um restaurante") // ATIVIDADE 2.2
    public ResponseEntity<List<PedidoResumoDTO>> buscarPedidosPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId) {
        List<PedidoResumoDTO> pedidos = pedidoService.buscarPedidosPorRestaurante(restauranteId);
        return ResponseEntity.ok(pedidos);
    }
}