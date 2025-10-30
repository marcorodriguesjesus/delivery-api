package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.entities.Produto;
import com.deliverytech.delivery.entities.Restaurante;
import com.deliverytech.delivery.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produtos")
@CrossOrigin("*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    /*
        Cadastrar novo Produto
     */
    @PostMapping
    public ResponseEntity<?> cadastrarProduto(@Validated @RequestBody Produto produto) {
        try {
            Produto produtoSalvo = produtoService.cadastrar(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Cadastrar Produto por ID do Restaurante
     */
    @PostMapping("/restaurante/{idRestaurante}")
    public ResponseEntity<?> cadastrarProdutoPorRestaurante(@PathVariable Long idRestaurante,
                                                            @Validated @RequestBody Produto produto) {
        try {
            Produto produtoSalvo = produtoService.cadastrarPorIdRestaurante(produto, idRestaurante);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Listar todos os Produtos disponíveis
     */
    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        List<Produto> produtos = produtoService.listarDisponiveis();
        return ResponseEntity.ok(produtos);
    }

    /*
        Listar todos os Produtos indisponíveis
     */
    @GetMapping("/indisponiveis")
    public ResponseEntity<List<Produto>> listarProdutosIndisponiveis() {
        List<Produto> produtos = produtoService.listarIndisponiveis();
        return ResponseEntity.ok(produtos);
    }

    /*
        Buscar Produto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Produto> produto = produtoService.buscarPorId(id);

        if (produto.isPresent()) {
            return ResponseEntity.ok(produto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
        Verificar se Produto está disponível
     */
    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<?> verificarDisponibilidade(@PathVariable Long id) {
        try {
            boolean estaDisponivel = produtoService.produtoEstaDisponivel(id);
            return ResponseEntity.ok(java.util.Map.of("id", id, "disponivel", estaDisponivel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        }
    }

    /*
        Atualizar Produto
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable Long id, @Validated @RequestBody Produto produto) {
        try {
            Produto produtoAtualizado = produtoService.atualizar(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Atualizar preço do Produto
     */
    @PatchMapping("/{id}/preco")
    public ResponseEntity<?> atualizarPreco(@PathVariable Long id, @RequestParam Double preco) {
        try {
            Produto produto = produtoService.atualizarPreco(id, preco);
            return ResponseEntity.ok(produto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Tornar Produto indisponível (exclusão lógica)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirProduto(@PathVariable Long id) {
        try {
            produtoService.indisponibilizarProduto(id);
            return ResponseEntity.ok().body("Produto excluído com sucesso");
        } catch(IllegalArgumentException excecao) {
            return ResponseEntity.badRequest().body("erro: " + excecao.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Tornar Produto disponível novamente
     */
    @PatchMapping("/{id}/disponivel")
    public ResponseEntity<?> tornarProdutoDisponivel(@PathVariable Long id) {
        try {
            Produto produto = produtoService.tornarDisponivel(id);
            return ResponseEntity.ok(produto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Buscar Produto por nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNome(@RequestParam String nome) {
        List<Produto> produto = produtoService.buscarPorNome(nome);
        if (produto != null) {
            return ResponseEntity.ok(produto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
        Buscar Produtos por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(produtos);
    }

    /*
        Buscar Produtos por Restaurante
     */
    @PostMapping("/restaurante")
    public ResponseEntity<List<Produto>> buscarPorRestaurante(@RequestBody Restaurante restaurante) {
        List<Produto> produtos = produtoService.buscarPorRestaurante(restaurante);
        return ResponseEntity.ok(produtos);
    }

    /*
        Buscar Produtos por ID do Restaurante
     */
    @GetMapping("/restaurante/{idRestaurante}/todos")
    public ResponseEntity<List<Produto>> buscarPorIdRestaurante(@PathVariable Long idRestaurante) {
        List<Produto> produtos = produtoService.buscarPorIdRestaurante(idRestaurante);
        return ResponseEntity.ok(produtos);
    }

    /*
        Buscar Produtos Disponíveis por ID do Restaurante
     */
    @GetMapping("/restaurante/{idRestaurante}/disponiveis")
    public ResponseEntity<List<Produto>> buscarDisponiveisPorIdRestaurante(@PathVariable Long idRestaurante) {
        List<Produto> produtos = produtoService.buscarDisponiveisPorIdRestaurante(idRestaurante);
        return ResponseEntity.ok(produtos);
    }

    /*
        Buscar Produtos por Faixa de Preço
     */
    @GetMapping("/faixa-preco")
    public ResponseEntity<?> buscarPorFaixaPreco(@RequestParam Double precoMinimo, @RequestParam Double precoMaximo) {
        try {
            List<Produto> produtos = produtoService.buscarPorFaixaPreco(precoMinimo, precoMaximo);
            return ResponseEntity.ok(produtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        }
    }
}