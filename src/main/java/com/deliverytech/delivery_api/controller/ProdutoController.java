package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.services.ProdutoService;

import java.util.List;

@RestController
@RequestMapping("/api/produtos") // ATIVIDADE 2: Caminho base atualizado
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    /**
     * 2.3: POST /api/produtos - Cadastrar produto
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO produtoSalvo = produtoService.cadastrarProduto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
    }

    /**
     * 2.3: GET /api/produtos/{id} - Buscar por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarProdutoPorId(id));
    }

    /**
     * 2.3: GET /api/produtos/categoria/{categoria} - Por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorCategoria(categoria));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.2): GET /api/produtos/buscar?nome={nome}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(produtoService.buscarProdutosPorNome(nome));
    }

    /**
     * 2.3: PUT /api/produtos/{id} - Atualizar produto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO atualizado = produtoService.atualizarProduto(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    /**
     * 2.3: PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
     */
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<ProdutoResponseDTO> alterarDisponibilidade(@PathVariable Long id, @RequestParam boolean disponivel) {
        ProdutoResponseDTO produto = produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.ok(produto);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.2): DELETE /api/produtos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }
}