package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.entities.Cliente;
import com.deliverytech.delivery.entities.Pedido;
import com.deliverytech.delivery.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin("*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /*
        Cadastrar novo Pedido
     */
    @PostMapping
    public ResponseEntity<?> cadastrarPedido(@Validated @RequestBody Pedido pedido) {
        try {
            Pedido pedidoSalvo = pedidoService.cadastrar(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Listar todos os Pedidos
     */
    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        List<Pedido> pedidos = pedidoService.listarTodos();
        return ResponseEntity.ok(pedidos);
    }

    /*
        Buscar Pedido por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoService.buscarPorId(id);

        if (pedido.isPresent()) {
            return ResponseEntity.ok(pedido.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
        Atualizar Pedido
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPedido(@PathVariable Long id, @Validated @RequestBody Pedido pedido) {
        try {
            Pedido pedidoAtualizado = pedidoService.atualizar(id, pedido);
            return ResponseEntity.ok(pedidoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Excluir Pedido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirPedido(@PathVariable Long id) {
        try {
            pedidoService.excluirPedido(id);
            return ResponseEntity.ok().body("Pedido excluído com sucesso");
        } catch(IllegalArgumentException excecao) {
            return ResponseEntity.badRequest().body("erro: " + excecao.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Buscar Pedidos por Cliente
     */
    @PostMapping("/cliente")
    public ResponseEntity<List<Pedido>> buscarPorCliente(@RequestBody Cliente cliente) {
        List<Pedido> pedidos = pedidoService.buscarPorCliente(cliente);
        return ResponseEntity.ok(pedidos);
    }

    /*
        Buscar Pedidos por Status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pedido>> buscarPorStatus(@PathVariable String status) {
        List<Pedido> pedidos = pedidoService.buscarPorStatus(status);
        return ResponseEntity.ok(pedidos);
    }

    /*
        Buscar Pedidos por Cliente e Status
     */
    @PostMapping("/cliente/status/{status}")
    public ResponseEntity<List<Pedido>> buscarPorClienteEStatus(@RequestBody Cliente cliente,
                                                                @PathVariable String status) {
        List<Pedido> pedidos = pedidoService.buscarPorClienteEStatus(cliente, status);
        return ResponseEntity.ok(pedidos);
    }

    /*
        Buscar Pedidos por Período
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<Pedido>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        List<Pedido> pedidos = pedidoService.buscarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(pedidos);
    }

    /*
        Buscar Pedidos por Cliente e Período
     */
    @PostMapping("/cliente/periodo")
    public ResponseEntity<List<Pedido>> buscarPorClienteEPeriodo(
            @RequestBody Cliente cliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        List<Pedido> pedidos = pedidoService.buscarPorClienteEPeriodo(cliente, dataInicio, dataFim);
        return ResponseEntity.ok(pedidos);
    }

    /*
        Calcular total do pedido
     */
    @PostMapping("/calcular")
    public ResponseEntity<?> calcularTotal(@RequestBody List<Long> idsProdutos) {
        try {
            Double total = pedidoService.calcularTotalPedido(idsProdutos);
            return ResponseEntity.ok(java.util.Map.of("total", total));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Confirmar Pedido (PENDENTE -> CONFIRMADO)
     */
    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.confirmarPedido(id);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Entregar Pedido (CONFIRMADO -> ENTREGUE)
     */
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<?> entregarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.entregarPedido(id);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Cancelar Pedido
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(id);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Atualizar Status do Pedido manualmente
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Pedido pedido = pedidoService.atualizarStatus(id, status);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }
}