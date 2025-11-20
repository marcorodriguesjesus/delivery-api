package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ApiResponse;
import com.deliverytech.delivery_api.dto.PagedResponse;
import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "Gerenciamento do cardápio (itens, preços e disponibilidade)")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    @Operation(summary = "Cadastrar produto", description = "Adiciona um novo item ao cardápio. Requer role ADMIN ou RESTAURANTE.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos ou restaurante inexistente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sem permissão para cadastrar produtos")
    })
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> cadastrar(
            @Valid @RequestBody ProdutoRequestDTO dto) {

        ProdutoResponseDTO produtoSalvo = produtoService.cadastrarProduto(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(produtoSalvo.getId()).toUri();

        return ResponseEntity.created(location).body(ApiResponse.success(produtoSalvo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Retorna os detalhes de um produto específico.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> buscarPorId(
            @Parameter(description = "ID do produto", example = "101") @PathVariable Long id) {

        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(produto));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Listar por categoria", description = "Busca paginada de produtos filtrados pelo tipo de culinária.")
    public ResponseEntity<ApiResponse<PagedResponse<ProdutoResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria", example = "Hambúrguer") @PathVariable String categoria,
            @Parameter(description = "Paginação") @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<ProdutoResponseDTO> page = produtoService.buscarProdutosPorCategoria(categoria, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar por nome", description = "Pesquisa produtos contendo o termo informado (case insensitive).")
    public ResponseEntity<ApiResponse<PagedResponse<ProdutoResponseDTO>>> buscarPorNome(
            @Parameter(description = "Termo de busca", example = "Pizza") @RequestParam String nome,
            @Parameter(description = "Paginação") @PageableDefault(size = 10) Pageable pageable) {

        Page<ProdutoResponseDTO> page = produtoService.buscarProdutosPorNome(nome, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @produtoService.isOwner(#id))")
    @Operation(summary = "Atualizar produto", description = "Atualiza dados de um produto. O restaurante só pode alterar seus próprios produtos.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produto atualizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Não autorizado a alterar este produto"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dto) {

        ProdutoResponseDTO atualizado = produtoService.atualizarProduto(id, dto);
        return ResponseEntity.ok(ApiResponse.success(atualizado));
    }

    @PatchMapping("/{id}/disponibilidade")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @produtoService.isOwner(#id))")
    @Operation(summary = "Alterar disponibilidade", description = "Ativa ou inativa um produto no cardápio rapidamente.")
    public ResponseEntity<ApiResponse<ProdutoResponseDTO>> alterarDisponibilidade(
            @PathVariable Long id,
            @Parameter(description = "Novo status") @RequestParam boolean disponivel) {

        ProdutoResponseDTO produto = produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.ok(ApiResponse.success(produto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @produtoService.isOwner(#id)")
    @Operation(summary = "Remover produto", description = "Exclui permanentemente um produto do cardápio.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Produto removido com sucesso (sem conteúdo)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Não autorizado a remover este produto"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }
}