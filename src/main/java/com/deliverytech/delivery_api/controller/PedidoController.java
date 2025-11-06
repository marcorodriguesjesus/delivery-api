package com.deliverytech.delivery_api.controller;

import java.math.BigDecimal;
import java.util.List;

import com.deliverytech.delivery_api.dto.PedidoRequestDTO;
import com.deliverytech.delivery_api.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.services.PedidoService;

@RestController
@RequestMapping("/api/pedidos") // ATIVIDADE 2: Caminho base atualizado
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // (Necessário para o endpoint de cálculo)
    @Autowired
    private RestauranteRepository restauranteRepository;

    /**
     * 2.4: POST /api/pedidos - Criar pedido (transação complexa)
     */
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO pedido = pedidoService.criarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    /**
     * 2.4: GET /api/pedidos/{id} - Buscar pedido completo
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPedidoPorId(id));
    }

    /**
     * 2.4: PATCH /api/pedidos/{id}/status - Atualizar status
     * (Usando @RequestParam em vez de PathVariable para o Status)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatus(@PathVariable Long id, @RequestParam StatusPedido status) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id, status);
        return ResponseEntity.ok(pedido);
    }

    /**
     * 2.4: DELETE /api/pedidos/{id} - Cancelar pedido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@PathVariable Long id) {
        PedidoResponseDTO pedidoCancelado = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(pedidoCancelado);
    }

    /**
     * 2.4: POST /api/pedidos/calcular - Calcular total sem salvar
     * (Reutilizando PedidoRequestDTO para obter itens e restauranteId)
     */
    @PostMapping("/calcular")
    public ResponseEntity<BigDecimal> calcularTotal(@Valid @RequestBody PedidoRequestDTO dto) {
        // Busca a taxa de entrega para o cálculo
        Restaurante r = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        BigDecimal total = pedidoService.calcularTotalPedido(dto.getItens(), dto.getRestauranteId(), r.getTaxaEntrega());
        return ResponseEntity.ok(total);
    }
}