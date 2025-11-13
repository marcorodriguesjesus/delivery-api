package com.deliverytech.delivery_api.controller;

import java.util.List;

import com.deliverytech.delivery_api.dto.ClienteResponseDTO;
import com.deliverytech.delivery_api.dto.ClienteResquestDTO;
import com.deliverytech.delivery_api.dto.PedidoResumoDTO;
import com.deliverytech.delivery_api.services.ClienteService;
import com.deliverytech.delivery_api.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes") // ATIVIDADE 2.2
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * 2.1: POST /api/clientes - Cadastrar cliente
     */
    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente") // ATIVIDADE 2.2
    public ResponseEntity<ClienteResponseDTO> cadastrar(@Valid @RequestBody ClienteResquestDTO clienteDto) {
        ClienteResponseDTO clienteSalvo = clienteService.cadastrarCliente(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
    }

    /**
     * 2.1: GET /api/clientes - Listar clientes ativos
     */
    @GetMapping
    @Operation(summary = "Listar todos os clientes ativos") // ATIVIDADE 2.2
    public ResponseEntity<List<ClienteResponseDTO>> listarAtivos() {
        List<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * 2.1: GET /api/clientes/{id} - Buscar por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um cliente específico pelo ID") // ATIVIDADE 2.2
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @Parameter(description = "ID do cliente a ser buscado", example = "1") // ATIVIDADE 2.2
            @PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * 2.1: GET /api/clientes/email/{email} - Buscar por email
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar um cliente específico pelo Email") // ATIVIDADE 2.2
    public ResponseEntity<ClienteResponseDTO> buscarPorEmail(
            @Parameter(description = "Email do cliente a ser buscado", example = "joao.silva@email.com") // ATIVIDADE 2.2
            @PathVariable String email) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

    /**
     * 2.1: PUT /api/clientes/{id} - Atualizar cliente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um cliente") // ATIVIDADE 2.2
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @Parameter(description = "ID do cliente a ser atualizado", example = "1") // ATIVIDADE 2.2
            @PathVariable Long id,
            @Valid @RequestBody ClienteResquestDTO clienteDto) {
        ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDto);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * 2.1: PATCH /api/clientes/{id}/status - Ativar/desativar
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar ou desativar um cliente (toggle)") // ATIVIDADE 2.2
    public ResponseEntity<ClienteResponseDTO> ativarDesativar(
            @Parameter(description = "ID do cliente", example = "1") // ATIVIDADE 2.2
            @PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(cliente);
    }
}