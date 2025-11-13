package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.dto.RestauranteRequestDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.services.ProdutoService;
import com.deliverytech.delivery_api.services.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Endpoints para gerenciamento de restaurantes e seus cardápios") // ATIVIDADE 2.2
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ProdutoService produtoService;

    /**
     * 2.2: POST /api/restaurantes - Cadastrar restaurante
     */
    @PostMapping
    @Operation(summary = "Cadastrar um novo restaurante") // ATIVIDADE 2.2
    public ResponseEntity<RestauranteResponseDTO> cadastrar(@Valid @RequestBody RestauranteRequestDTO dto) {
        RestauranteResponseDTO restauranteSalvo = restauranteService.cadastrarRestaurante(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteSalvo);
    }

    /**
     * MODIFICADO (ATIVIDADE 1.1) para aceitar filtros
     */
    @GetMapping
    @Operation(summary = "Listar restaurantes com filtros opcionais") // ATIVIDADE 2.2
    public ResponseEntity<List<RestauranteResponseDTO>> listarRestaurantes(
            @Parameter(description = "Filtrar por tipo de culinária", example = "Italiana") // ATIVIDADE 2.2
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar por status (true=ativos, false=inativos)") // ATIVIDADE 2.2
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantes(categoria, ativo));
    }

    /**
     * 2.2: GET /api/restaurantes/{id} - Buscar por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um restaurante específico pelo ID") // ATIVIDADE 2.2
    public ResponseEntity<RestauranteResponseDTO> buscarPorId(
            @Parameter(description = "ID do restaurante", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantePorId(id));
    }

    /**
     * 2.2: GET /api/restaurantes/categoria/{categoria} - Por categoria
     */
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar restaurantes por categoria (tipo de culinária)") // ATIVIDADE 2.2
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria", example = "Italiana") @PathVariable String categoria) {
        return ResponseEntity.ok(restauranteService.buscarRestaurantesPorCategoria(categoria));
    }

    /**
     * 2.2: PUT /api/restaurantes/{id} - Atualizar restaurante
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um restaurante") // ATIVIDADE 2.2
    public ResponseEntity<RestauranteResponseDTO> atualizar(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Valid @RequestBody RestauranteRequestDTO dto) {
        RestauranteResponseDTO atualizado = restauranteService.atualizarRestaurante(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.1): PATCH /api/restaurantes/{id}/status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar ou desativar um restaurante (toggle)") // ATIVIDADE 2.2
    public ResponseEntity<RestauranteResponseDTO> ativarDesativar(
            @Parameter(description = "ID do restaurante") @PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.ativarDesativarRestaurante(id);
        return ResponseEntity.ok(restaurante);
    }

    /**
     * 2.2: GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa
     */
    @GetMapping("/{id}/taxa-entrega/{cep}")
    @Operation(summary = "Calcular taxa de entrega para um CEP (lógica de exemplo)") // ATIVIDADE 2.2
    public ResponseEntity<BigDecimal> calcularTaxa(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Parameter(description = "CEP do destino", example = "30110000") @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxa);
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.1): GET /api/restaurantes/proximos/{cep}
     */
    @GetMapping("/proximos/{cep}")
    @Operation(summary = "Buscar restaurantes próximos a um CEP (lógica de exemplo)") // ATIVIDADE 2.2
    public ResponseEntity<List<RestauranteResponseDTO>> buscarProximos(
            @Parameter(description = "CEP do cliente", example = "30110000") @PathVariable String cep) {
        List<RestauranteResponseDTO> restaurantes = restauranteService.buscarRestaurantesProximos(cep);
        return ResponseEntity.ok(restaurantes);
    }

    /**
     * 2.3: GET /api/restaurantes/{restauranteId}/produtos - Produtos do restaurante
     */
    @GetMapping("/{restauranteId}/produtos")
    @Operation(summary = "Listar todos os produtos disponíveis de um restaurante") // ATIVIDADE 2.2
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId);
        return ResponseEntity.ok(produtos);
    }
}