package com.deliverytech.delivery_api.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.services.ProdutoService;


@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {
    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@Validated @RequestBody Produto produto) {
        try {
            Produto produtoSalvo = produtoService.cadastrar(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    // Listar todos os produtos
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            return ResponseEntity.ok(produtoService.listarTodos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }
    // Buscar produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Produto produto = produtoService.buscarPorId(id);
            if (produto != null) {
                return ResponseEntity.ok(produto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }
    // Atualizar produto
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Validated @RequestBody Produto produto) {
        try {
            Produto atualizado = produtoService.atualizar(id, produto);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }
    // Excluir produto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        try {
            produtoService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }
    //inativar produto
    @PutMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            Produto produtoInativado = produtoService.inativar(id);
            return ResponseEntity.ok(produtoInativado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

    // buscar produto por restaurante ID
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<?> buscarPorRestaurante(@PathVariable Long restauranteId) {
        try {
            return ResponseEntity.ok(produtoService.buscarPorRestaurante(restauranteId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno do servidor");
        }
    }

}
