package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.entities.Restaurante;
import com.deliverytech.delivery.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurantes")
@CrossOrigin("*")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    /*
        Cadastrar novo Restaurante
     */

    @PostMapping
    public ResponseEntity<?> cadastrarRestaurante(@Validated @RequestBody Restaurante restaurante) {
        try {
            Restaurante restauranteSalvo = restauranteService.cadastrar(restaurante);
            return ResponseEntity.status(HttpStatus.CREATED).body(restauranteSalvo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Listar todos os Restaurantes online/abertos
     */
    @GetMapping
    public ResponseEntity<List<Restaurante>> listarRestaurantes() {
        List<Restaurante> restaurantes = restauranteService.listarOnline();
        return ResponseEntity.ok(restaurantes);
    }

    /*
        Listar todos os Restaurantes online (explícito)
     */
    @GetMapping("/online")
    public ResponseEntity<List<Restaurante>> listarRestaurantesOnline() {
        List<Restaurante> restaurantes = restauranteService.listarOnline();
        return ResponseEntity.ok(restaurantes);
    }

    /*
        Buscar Restaurante por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Restaurante> restaurante = restauranteService.buscarPorId(id);

        if (restaurante.isPresent()) {
            return ResponseEntity.ok(restaurante.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
        Atualizar Restaurante
     */

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarRestaurante(@PathVariable Long id, @Validated @RequestBody Restaurante restaurante) {
        try {
            Restaurante restauranteAtualizado = restauranteService.atualizar(id, restaurante);
            return ResponseEntity.ok(restauranteAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Verificar se Restaurante está online
     */

    @GetMapping("/{id}/status")
    public ResponseEntity<?> verificarStatus(@PathVariable Long id) {
        try {
            boolean estaOnline = restauranteService.restauranteEstaOnline(id);
            return ResponseEntity.ok(java.util.Map.of(
                    "id", id,
                    "online", estaOnline,
                    "status", estaOnline ? "ABERTO" : "FECHADO"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("erro: " + e.getMessage());
        }
    }

    /*
        Definir Restaurante como offline/fechado
     */
    @PatchMapping("/{id}/offline")
    public ResponseEntity<?> definirRestauranteOffline(@PathVariable Long id) {
        try {
            Restaurante restaurante = restauranteService.definirOffline(id);
            return ResponseEntity.ok(restaurante);
        } catch(IllegalArgumentException excecao) {
            return ResponseEntity.badRequest().body("erro: " + excecao.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Definir Restaurante como online/aberto
     */
    @PatchMapping("/{id}/online")
    public ResponseEntity<?> definirRestauranteOnline(@PathVariable Long id) {
        try {
            Restaurante restaurante = restauranteService.definirOnline(id);
            return ResponseEntity.ok(restaurante);
        } catch(IllegalArgumentException excecao) {
            return ResponseEntity.badRequest().body("erro: " + excecao.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Alternar status do Restaurante (online ↔ offline)
     */
    @PatchMapping("/{id}/alternar")
    public ResponseEntity<?> alternarStatusRestaurante(@PathVariable Long id) {
        try {
            Restaurante restaurante = restauranteService.alternarStatus(id);
            return ResponseEntity.ok(restaurante);
        } catch(IllegalArgumentException excecao) {
            return ResponseEntity.badRequest().body("erro: " + excecao.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro Interno do Servidor");
        }
    }

    /*
        Buscar Restaurante por nome
     */

    @GetMapping("/buscar")
    public ResponseEntity<List<Restaurante>> buscarPorNome(@RequestParam String nome) {
        List<Restaurante> restaurantes = restauranteService.buscarPorNome(nome);
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Restaurante>> buscarPorCategoria(@PathVariable String categoria) {
        List<Restaurante> restaurantes = restauranteService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    @GetMapping("/avaliacao/{avaliacao}")
    public ResponseEntity<?> buscarPorAvaliacao(@PathVariable Double avaliacao) {
        List<Restaurante> restaurantes = restauranteService.buscarPorAvaliacao(avaliacao);
        return ResponseEntity.ok(restaurantes);
    }
}
