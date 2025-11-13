package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.services.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos (cardápio)") // ATIVIDADE 2.2
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    /**
     * 2.3: POST /api/produtos - Cadastrar produto
     */
    @PostMapping
    @Operation(summary = "Cadastrar um novo produto (item de cardápio)") // ATIVIDADE 2.2
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO produtoSalvo = produtoService.cadastrarProduto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    /**
     * 2.3: GET /api/produtos/{id} - Buscar por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um produto específico pelo ID") // ATIVIDADE 2.2
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "ID do produto", example = "101") @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarProdutoPorId(id));
    }

    /**
     * 2.3: GET /api/produtos/categoria/{categoria} - Por categoria
     */
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar produtos pela categoria (em todos restaurantes)") // ATIVIDADE 2.2
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria", example = "Hambúrguer") @PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.2): GET /api/produtos/buscar?nome={nome}
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar produtos por nome (em todos restaurantes)") // ATIVIDADE 2.2
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(
            @Parameter(description = "Termo de busca (parcial)", example = "Pizza") @RequestParam String nome) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorNome(nome));
    }

    /**
     * 2.3: PUT /api/produtos/{id} - Atualizar produto
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um produto") // ATIVIDADE 2.2
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO atualizado = produtoService.atualizarProduto(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    /**
     * 2.3: PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
     */
    @PatchMapping("/{id}/disponibilidade")
    @Operation(summary = "Ativar ou desativar a disponibilidade de um produto") // ATIVIDADE 2.2
    public ResponseEntity<ProdutoResponseDTO> alterarDisponibilidade(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Parameter(description = "Status (true=disponível, false=indisponível)") @RequestParam boolean disponivel) {
        ProdutoResponseDTO produto = produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.ok(produto);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.2): DELETE /api/produtos/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um produto (exclusão física)") // ATIVIDADE 2.2
    public ResponseEntity<Void> remover(
            @Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }
}