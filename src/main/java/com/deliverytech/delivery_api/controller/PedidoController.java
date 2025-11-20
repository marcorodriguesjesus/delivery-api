package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.services.PedidoService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Fluxo de checkout e acompanhamento de pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Criar novo pedido", description = "Registra um pedido contendo múltiplos itens. Valida se produtos pertencem ao restaurante e calcula o total.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Erro de validação (ex: produto de outro restaurante, restaurante fechado)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Apenas clientes podem criar pedidos")
    })
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> criarPedido(
            @Valid @RequestBody PedidoRequestDTO dto) {

        PedidoResponseDTO pedido = pedidoService.criarPedido(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(pedido.getId()).toUri();

        return ResponseEntity.created(location).body(ApiResponse.success(pedido));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os pedidos (Admin)", description = "Endpoint administrativo para visão geral com filtros.")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> listarPedidos(
            @Parameter(description = "Filtrar por status", example = "PENDENTE")
            @RequestParam(required = false) StatusPedido status,

            @Parameter(description = "Início do período (ISO DateTime)", example = "2025-10-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,

            @Parameter(description = "Fim do período (ISO DateTime)", example = "2025-10-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,

            @Parameter(description = "Paginação")
            @PageableDefault(size = 10, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PedidoResumoDTO> page = pedidoService.listarPedidos(status, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pedidoService.canAccess(#id)")
    @Operation(summary = "Detalhes do pedido", description = "Retorna o pedido completo com itens e dados do cliente/restaurante.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Você não tem permissão para ver este pedido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> buscarPorId(
            @PathVariable Long id) {

        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(pedido));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status", description = "Avança o status do pedido (ex: PENDENTE -> PREPARANDO).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status atualizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Transição de status inválida (ex: tentar confirmar pedido já cancelado)")
    })
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> atualizarStatus(
            @PathVariable Long id,
            @Parameter(description = "Novo status") @RequestParam StatusPedido status) {

        PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id, status);
        return ResponseEntity.ok(ApiResponse.success(pedido));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido se ele ainda não tiver saído para entrega.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Não é possível cancelar (já entregue ou saiu para entrega)")
    })
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calcular")
    @Operation(summary = "Simular total", description = "Calcula o valor total do pedido (produtos + frete) sem salvar no banco.")
    public ResponseEntity<ApiResponse<BigDecimal>> calcularTotal(@Valid @RequestBody PedidoRequestDTO dto) {
        Restaurante r = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        BigDecimal total = pedidoService.calcularTotalPedido(dto.getItens(), dto.getRestauranteId(), r.getTaxaEntrega());
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Meus Pedidos (Cliente)", description = "Histórico de pedidos do usuário logado.")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> meusPedidos(
            @PageableDefault(size = 10, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {

        Long clienteId = securityUtils.getCurrentUserId();
        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosPorCliente(clienteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    @GetMapping("/recebidos")
    @PreAuthorize("hasRole('RESTAURANTE')")
    @Operation(summary = "Pedidos Recebidos (Restaurante)", description = "Fila de pedidos do restaurante logado.")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> pedidosDoRestaurante(
            @PageableDefault(size = 10, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {

        Long restauranteId = securityUtils.getCurrentRestauranteId();
        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosPorRestaurante(restauranteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    // Endpoints legados mantidos para compatibilidade, mas documentados
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Histórico por Cliente ID", description = "Busca administrativa de pedidos de um cliente específico.")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> buscarPedidosPorCliente(
            @PathVariable Long clienteId,
            @PageableDefault(size = 5, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosPorCliente(clienteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    @GetMapping("/restaurante/{restauranteId}")
    @Operation(summary = "Histórico por Restaurante ID", description = "Busca administrativa de pedidos de um restaurante específico.")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> buscarPedidosPorRestaurante(
            @PathVariable Long restauranteId,
            @PageableDefault(size = 5, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosPorRestaurante(restauranteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }
}