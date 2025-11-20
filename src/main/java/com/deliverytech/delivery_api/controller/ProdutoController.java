package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ApiResponse;
import com.deliverytech.delivery_api.dto.PagedResponse;
import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.services.ProdutoService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*") // 3.3: CORS
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos (cardápio)")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    /**
     * 2.3: POST /api/produtos - Cadastrar produto
     * ATIVIDADE 3.1, 3.2, 3.3: Retorna 201 com Location e ApiResponse
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    @Operation(summary = "Cadastrar um novo produto (item de cardápio)")
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> cadastrar(
            @Valid @RequestBody ProdutoRequestDTO dto) {

        ProdutoResponseDTO produtoSalvo = produtoService.cadastrarProduto(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(produtoSalvo.getId()).toUri();

        return ResponseEntity.created(location).body(ApiResponse.success(produtoSalvo));
    }

    /**
     * 2.3: GET /api/produtos/{id} - Buscar por ID
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um produto específico pelo ID")
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> buscarPorId(
            @Parameter(description = "ID do produto", example = "101") @PathVariable Long id) {

        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(produto));
    }

    /**
     * 2.3: GET /api/produtos/categoria/{categoria} - Por categoria
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrappers
     */
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar produtos pela categoria (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<ProdutoResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria", example = "Hambúrguer") @PathVariable String categoria,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<ProdutoResponseDTO> page = produtoService.buscarProdutosPorCategoria(categoria, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.2): GET /api/produtos/buscar?nome={nome}
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrappers
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar produtos por nome (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<ProdutoResponseDTO>>> buscarPorNome(
            @Parameter(description = "Termo de busca (parcial)", example = "Pizza") @RequestParam String nome,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ProdutoResponseDTO> page = produtoService.buscarProdutosPorNome(nome, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * 2.3: PUT /api/produtos/{id} - Atualizar produto
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @produtoService.isOwner(#id))")
    @Operation(summary = "Atualizar os dados de um produto")
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> atualizar(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dto) {

        ProdutoResponseDTO atualizado = produtoService.atualizarProduto(id, dto);
        return ResponseEntity.ok(ApiResponse.success(atualizado));
    }

    /**
     * 2.3: PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @PatchMapping("/{id}/disponibilidade")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @produtoService.isOwner(#id))")
    @Operation(summary = "Ativar ou desativar a disponibilidade de um produto")
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> alterarDisponibilidade(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Parameter(description = "Status (true=disponível, false=indisponível)") @RequestParam boolean disponivel) {

        ProdutoResponseDTO produto = produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.ok(ApiResponse.success(produto));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.2): DELETE /api/produtos/{id}
     * ATIVIDADE 3.1: Modificado para retornar 204 No Content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @Operation(summary = "Remover um produto (exclusão física)")
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do produto") @PathVariable Long id) {

        produtoService.removerProduto(id);

        // 3.1: Retorna 204 No Content (corpo vazio)
        return ResponseEntity.noContent().build();
    }
}