package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.*;
import com.deliverytech.delivery_api.services.ProdutoService;
import com.deliverytech.delivery_api.services.RestauranteService;
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

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*") // 3.3: CORS
@Tag(name = "Restaurantes", description = "Endpoints para gerenciamento de restaurantes e seus cardápios")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @Autowired
    private ProdutoService produtoService; // Necessário para o endpoint 2.3

    /**
     * 2.2: POST /api/restaurantes - Cadastrar restaurante
     * ATIVIDADE 3.1, 3.2, 3.3: Retorna 201 com Location e ApiResponse
     */
    @PostMapping
    @Operation(summary = "Cadastrar um novo restaurante")
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> cadastrar(
            @Valid @RequestBody RestauranteRequestDTO dto) {

        RestauranteResponseDTO restauranteSalvo = restauranteService.cadastrarRestaurante(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(restauranteSalvo.getId()).toUri();

        return ResponseEntity.created(location).body(ApiResponse.success(restauranteSalvo));
    }

    /**
     * 2.2: GET /api/restaurantes - Listar disponíveis
     * ATIVIDADE 3.2, 3.4: Adiciona paginação, filtros e wrappers
     */
    @GetMapping
    @Operation(summary = "Listar restaurantes com filtros opcionais (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<RestauranteResponseDTO>>> listarRestaurantes(
            @Parameter(description = "Filtrar por tipo de culinária", example = "Italiana")
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtrar por status (true=ativos, false=inativos)")
            @RequestParam(required = false) Boolean ativo,
            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<RestauranteResponseDTO> page = restauranteService.buscarRestaurantes(categoria, ativo, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * 2.2: GET /api/restaurantes/{id} - Buscar por ID
     * ATIVIDADE 3.2, 3.3: Adiciona ApiResponse e Cache
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um restaurante específico pelo ID")
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> buscarPorId(
            @Parameter(description = "ID do restaurante", example = "1") @PathVariable Long id) {

        RestauranteResponseDTO dto = restauranteService.buscarRestaurantePorId(id);
        CacheControl cache = CacheControl.maxAge(5, TimeUnit.MINUTES);
        return ResponseEntity.ok().cacheControl(cache).body(ApiResponse.success(dto));
    }

    /**
     * 2.2: GET /api/restaurantes/categoria/{categoria} - Por categoria
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrappers
     */
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar restaurantes por categoria (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<RestauranteResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Nome da categoria", example = "Italiana") @PathVariable String categoria,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<RestauranteResponseDTO> page = restauranteService.buscarRestaurantesPorCategoria(categoria, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * 2.2: PUT /api/restaurantes/{id} - Atualizar restaurante
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um restaurante")
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> atualizar(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Valid @RequestBody RestauranteRequestDTO dto) {

        RestauranteResponseDTO atualizado = restauranteService.atualizarRestaurante(id, dto);
        return ResponseEntity.ok(ApiResponse.success(atualizado));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.1): PATCH /api/restaurantes/{id}/status
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar ou desativar um restaurante (toggle)")
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> ativarDesativar(
            @Parameter(description = "ID do restaurante") @PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.ativarDesativarRestaurante(id);
        return ResponseEntity.ok(ApiResponse.success(restaurante));
    }

    /**
     * 2.2: GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa
     * ATIVIDADE 3.2: Adiciona ApiResponse
     */
    @GetMapping("/{id}/taxa-entrega/{cep}")
    @Operation(summary = "Calcular taxa de entrega para um CEP (lógica de exemplo)")
    public ResponseEntity<ApiResponse<BigDecimal>> calcularTaxa(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Parameter(description = "CEP do destino", example = "30110000") @PathVariable String cep) {

        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        // Respostas simples também podem ser envelopadas
        return ResponseEntity.ok(ApiResponse.success(taxa));
    }

    /**
     * NOVO ENDPOINT (ATIVIDADE 1.1): GET /api/restaurantes/proximos/{cep}
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrappers
     */
    @GetMapping("/proximos/{cep}")
    @Operation(summary = "Buscar restaurantes próximos a um CEP (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<RestauranteResponseDTO>>> buscarProximos(
            @Parameter(description = "CEP do cliente", example = "30110000") @PathVariable String cep,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 5, sort = "avaliacao") Pageable pageable) {

        Page<RestauranteResponseDTO> page = restauranteService.buscarRestaurantesProximos(cep, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }

    /**
     * 2.3: GET /api/restaurantes/{restauranteId}/produtos - Produtos do restaurante
     * ATIVIDADE 3.2, 3.4: Adiciona paginação e wrappers
     */
    @GetMapping("/{restauranteId}/produtos")
    @Operation(summary = "Listar todos os produtos disponíveis de um restaurante (paginado)")
    public ResponseEntity<ApiResponse<PagedResponse<ProdutoResponseDTO>>> buscarProdutosPorRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId,
            @Parameter(description = "Parâmetros de paginação")
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        Page<ProdutoResponseDTO> page = produtoService.buscarProdutosPorRestaurante(restauranteId, pageable);
        return ResponseEntity.ok(ApiResponse.success(new PagedResponse<>(page)));
    }
}