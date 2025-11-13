package com.deliverytech.delivery_api.controller;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.deliverytech.delivery_api.dto.*; // Importar todos os novos DTOs
import com.deliverytech.delivery_api.services.ClienteService;
import com.deliverytech.delivery_api.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*") // 3.3: CORS já está habilitado
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // PedidoService não é mais necessário aqui (endpoint /pedidos foi movido)

    /**
     * 2.1: POST /api/clientes - Cadastrar cliente
     * ATIVIDADE 3.1, 3.2, 3.3: Retorna 201 com Location Header e ApiResponse
     */
    @PostMapping
    @Operation(summary = "Cadastrar um novo cliente")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> cadastrar(
            @Valid @RequestBody ClienteResquestDTO clienteDto) {

        ClienteResponseDTO clienteSalvo = clienteService.cadastrarCliente(clienteDto);

        // 3.3: Criar o Header "Location"
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest() // Pega a URL base (http://.../api/clientes)
                .path("/{id}") // Adiciona /id
                .buildAndExpand(clienteSalvo.getId()) // Substitui {id}
                .toUri();

        // 3.1 e 3.2: Retorna 201 Created com o body padronizado
        return ResponseEntity.created(location).body(ApiResponse.success(clienteSalvo));
    }

    /**
     * 2.1: GET /api/clientes - Listar clientes ativos
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrapper de resposta
     */
    @GetMapping
    @Operation(summary = "Listar todos os clientes ativos (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<ClienteResponseDTO>>> listarAtivos(
            // 3.4: Recebe parâmetros de paginação (?page=0&size=10&sort=nome,asc)
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<ClienteResponseDTO> clientePage = clienteService.listarClientesAtivos(pageable);

        // 3.2: Envelopa a Página no PagedResponse e no ApiResponse
        PagedResponse<ClienteResponseDTO> pagedResponse = new PagedResponse<>(clientePage);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    /**
     * 2.1: GET /api/clientes/{id} - Buscar por ID
     * ATIVIDADE 3.2, 3.3: Adiciona Cache-Control e ApiResponse
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um cliente específico pelo ID")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> buscarPorId(
            @Parameter(description = "ID do cliente a ser buscado", example = "1")
            @PathVariable Long id) {

        ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);

        // 3.3: Adiciona Cache-Control de 5 minutos (exemplo)
        CacheControl cacheControl = CacheControl.maxAge(5, TimeUnit.MINUTES);

        // 3.2: Retorna 200 OK com cache e body padronizado
        return ResponseEntity.ok().cacheControl(cacheControl).body(ApiResponse.success(cliente));
    }

    /**
     * 2.1: GET /api/clientes/email/{email} - Buscar por email
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar um cliente específico pelo Email")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> buscarPorEmail(
            @Parameter(description = "Email do cliente a ser buscado", example = "joao.silva@email.com")
            @PathVariable String email) {

        ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(ApiResponse.success(cliente));
    }

    /**
     * 2.1: PUT /api/clientes/{id} - Atualizar cliente
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um cliente")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> atualizar(
            @Parameter(description = "ID do cliente a ser atualizado", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ClienteResquestDTO clienteDto) {

        ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDto);
        return ResponseEntity.ok(ApiResponse.success(clienteAtualizado));
    }

    /**
     * 2.1: PATCH /api/clientes/{id}/status - Ativar/desativar
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar ou desativar um cliente (toggle)")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> ativarDesativar(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long id) {

        ClienteResponseDTO cliente = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(ApiResponse.success(cliente));
    }

    /**
     * 2.4: GET /api/clientes/{clienteId}/pedidos - Histórico do cliente
     * ATIVIDADE 3: Este endpoint foi MOVIDO para PedidoController
     */
}