package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.dto.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.services.ProdutoService;
import com.deliverytech.delivery_api.services.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes") // ATIVIDADE 2: Caminho base atualizado
@CrossOrigin(origins = "*")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ProdutoService produtoService; // Necessário para o endpoint 2.3

    /**
     * 2.2: POST /api/restaurantes - Cadastrar restaurante
     */
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@Valid @RequestBody RestauranteRequestDTO dto) {
        RestauranteResponseDTO restauranteSalvo = restauranteService.cadastrarRestaurante(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteSalvo);
    }

    /**
     * 2.2: GET /api/restaurantes - Listar disponíveis
     * MODIFICADO (ATIVIDADE 1.1) para aceitar filtros
     */
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listarRestaurantes(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantes(categoria, ativo));
    }

    /**
     * 2.2: GET /api/restaurantes/{id} - Buscar por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantePorId(id));
    }

    /**
     * 2.2: GET /api/restaurantes/categoria/{categoria} - Por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantesPorCategoria(categoria));
    }

    /**
     * 2.2: PUT /api/restaurantes/{id} - Atualizar restaurante
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteRequestDTO dto) {
        RestauranteResponseDTO atualizado = restauranteService.atualizarRestaurante(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.1): PATCH /api/restaurantes/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<RestauranteResponseDTO> ativarDesativar(@PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.ativarDesativarRestaurante(id);
        return ResponseEntity.ok(restaurante);
    }

    /**
     * 2.2: GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa
     */
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxa(@PathVariable Long id, @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxa);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.1): GET /api/restaurantes/proximos/{cep}
     */
    @GetMapping("/proximos/{cep}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(@PathVariable String cep) {
        List<RestauranteResponseDTO> restaurantes = restauranteService.buscarRestaurantesProximos(cep);
        return ResponseEntity.ok(restaurantes);
    }

    /**
     * 2.3: GET /api/restaurantes/{restauranteId}/produtos - Produtos do restaurante
     * (Este endpoint pertence à Atividade 2.3, mas se encaixa melhor aqui)
     */
    @GetMapping("/{restauranteId}/produtos")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorRestaurante(@PathVariable Long restauranteId) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId);
        return ResponseEntity.ok(produtos);
    }
}