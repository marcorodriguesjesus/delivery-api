package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.services.PedidoService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // 3.3: CORS
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento do ciclo de vida dos pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * 2.4: POST /api/pedidos - Criar pedido
     * ATIVIDADE 3.1, 3.2, 3.3: Retorna 201 com Location e ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')") // Só cliente cria pedido
    @Operation(summary = "Criar um novo pedido (transação complexa)")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> criarPedido(
            @Valid @RequestBody PedidoRequestDTO dto) {

        PedidoResponseDTO pedido = pedidoService.criarPedido(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(pedido.getId()).toUri();

        return ResponseEntity.created(location).body(ApiResponse.success(pedido));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.3): GET /api/pedidos - Listar com filtros
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrappers
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Só ADMIN vê TUDO
    @Operation(summary = "Listar pedidos com filtros opcionais (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> listarPedidos(
            @Parameter(description = "Filtrar por status", example = "PENDENTE")
            @RequestParam(required = false) StatusPedido status,

            @Parameter(description = "Data/Hora inicial (formato ISO: YYYY-MM-DDTHH:MM:SS)", example = "2025-10-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,

            @Parameter(description = "Data/Hora final (formato ISO: YYYY-MM-DDTHH:MM:SS)", example = "2025-10-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,

            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 10, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PedidoResumoDTO> page = pedidoService.listarPedidos(status, dataInicio, dataFim, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * 2.4: GET /api/pedidos/{id} - Buscar pedido completo
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @GetMapping("/{id}")
    @PreAuthorize("@pedidoService.canAccess(#id)")
    @Operation(summary = "Buscar um pedido completo pelo ID")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> buscarPorId(
            @Parameter(description = "ID do pedido", example = "501") @PathVariable Long id) {

        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(pedido));
    }

    /**
     * 2.4: PATCH /api/pedidos/{id}/status - Atualizar status
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar o status de um pedido")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> atualizarStatus(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Parameter(description = "Novo status do pedido") @RequestParam StatusPedido status) {

        PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id, status);
        return ResponseEntity.ok(ApiResponse.success(pedido));
    }

    /**
     * 2.4: DELETE /api/pedidos/{id} - Cancelar pedido
     * ATIVIDADE 3.1: Modificado para retornar 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar um pedido (se as regras de negócio permitirem)")
    public ResponseEntity<Void> cancelarPedido(
            @Parameter(description = "ID do pedido") @PathVariable Long id) {

        pedidoService.cancelarPedido(id);

        // 3.1: Retorna 204 No Content
        return ResponseEntity.noContent().build();
    }

    /**
     * 2.4: POST /api/pedidos/calcular - Calcular total sem salvar
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @PostMapping("/calcular")
    @Operation(summary = "Calcular o total de um pedido (sem salvar no banco)")
    public ResponseEntity<ApiResponse<BigDecimal>> calcularTotal(@Valid @RequestBody PedidoRequestDTO dto) {
        Restaurante r = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

        BigDecimal total = pedidoService.calcularTotalPedido(dto.getItens(), dto.getRestauranteId(), r.getTaxaEntrega());
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.3): GET /api/pedidos/cliente/{clienteId}
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrappers
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Buscar o histórico de pedidos de um cliente (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> buscarPedidosPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 5, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosPorCliente(clienteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.3): GET /api/pedidos/restaurante/{restauranteId}
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrappers
     */
    @GetMapping("/restaurante/{restauranteId}")
    @Operation(summary = "Buscar os pedidos recebidos por um restaurante (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> buscarPedidosPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 5, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosPorRestaurante(restauranteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * NOVO ENDPOINT SEGURO: Meus Pedidos (Cliente)
     * Não recebe ID na URL, pega do token.
     */
    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Listar pedidos do cliente logado")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> meusPedidos(
            @PageableDefault(size = 10, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {

        Long clienteId = securityUtils.getCurrentUserId(); // ID do token! (Na verdade, usuário ID. Se Cliente ID != Usuario ID na sua modelagem, precisa ajustar aqui. Assumindo 1:1 por enquanto ou que Usuario tem link)

        // Nota: Se sua tabela 'clientes' for separada de 'usuarios', você precisa buscar o Cliente vinculado ao Usuario logado.
        // Assumindo para este exercício que o ID do Usuario é usado como ID do Cliente nas tabelas ou há um vínculo direto.

        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosPorCliente(clienteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * NOVO ENDPOINT SEGURO: Pedidos do Meu Restaurante
     */
    @GetMapping("/recebidos") // '/restaurante' pode confundir com filtro público
    @PreAuthorize("hasRole('RESTAURANTE')")
    @Operation(summary = "Listar pedidos recebidos pelo restaurante logado")
    public ResponseEntity<ApiResponse<PagedResponse<PedidoResumoDTO>>> pedidosDoRestaurante(
            @PageableDefault(size = 10, sort = "dataPedido", direction = Sort.Direction.DESC) Pageable pageable) {

        Long restauranteId = securityUtils.getCurrentRestauranteId();
        Page<PedidoResumoDTO> page = pedidoService.buscarPedidosPorRestaurante(restauranteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }
}