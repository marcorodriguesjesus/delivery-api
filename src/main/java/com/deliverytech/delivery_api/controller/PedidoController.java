package com.deliverytech.delivery_api.controller;
import java.util.List;

import com.deliverytech.delivery_api.entity.PedidoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.entity.Pedido;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.services.PedidoService;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    /**
     * Criar novo pedido
     */
    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoDTO dto) {
        try {
            Pedido pedido = pedidoService.criarPedido(dto);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }
    }
    /**
     * Listar pedidos por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Atualizar status do pedido
     */
    @PutMapping("/{pedidoId}/{status}")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long pedidoId,
                                            @PathVariable StatusPedido status) {
        try {
            Pedido pedido = pedidoService.atualizarStatus(pedidoId, status);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

}
