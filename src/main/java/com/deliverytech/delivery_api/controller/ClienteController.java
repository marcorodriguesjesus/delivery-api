package com.deliverytech.delivery_api.controller;

import java.util.List;

import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.ClienteResquestDTO;
import com.deliverytech.delivery_api.dto.PedidoResumoDTO;
import com.deliverytech.delivery_api.services.ClienteService;
import com.deliverytech.delivery_api.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes") // ATIVIDADE 2: Caminho base atualizado
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService; // Necessário para o endpoint 2.4

    /**
     * 2.1: POST /api/clientes - Cadastrar cliente
     */
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(@Valid @RequestBody ClienteResquestDTO clienteDto) {
        ClienteResponseDTO clienteSalvo = clienteService.cadastrarCliente(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    /**
     * 2.1: GET /api/clientes - Listar clientes ativos
     */
    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarAtivos() {
        List<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * 2.1: GET /api/clientes/{id} - Buscar por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * 2.1: GET /api/clientes/email/{email} - Buscar por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponseDTO> buscarPorEmail(@PathVariable String email) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

    /**
     * 2.1: PUT /api/clientes/{id} - Atualizar cliente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteResquestDTO clienteDto) {
        ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDto);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * 2.1: PATCH /api/clientes/{id}/status - Ativar/desativar
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClienteResponseDTO> ativarDesativar(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * 2.4: GET /api/clientes/{clienteId}/pedidos - Histórico do cliente
     * (Este endpoint foi MOVIDO para /api/pedidos/cliente/{clienteId}
     */
}