package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ATIVIDADE 4.1: Testes de Integração para RestauranteController (Filtros e Paginação)
 */
public class RestauranteControllerIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Cenário 4.1: Deve listar restaurantes com filtro e paginação")
    void testListarRestaurantes_FilterAndPagination() throws Exception {
        // data.sql insere 3 restaurantes, mas apenas 1 é "Italiana"
        mockMvc.perform(get("/api/restaurantes?categoria=Italiana&ativo=true&size=1&sort=nome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                // Valida a paginação
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                // Valida o conteúdo do filtro
                .andExpect(jsonPath("$.data.content[0].nome").value("Pizzaria Bella"));
    }

    @Test
    @DisplayName("Cenário 4.1: Deve listar produtos de um restaurante (paginado)")
    void testBuscarProdutosPorRestaurante_Pagination() throws Exception {
        // Restaurante 2 (Burger House) tem 3 produtos no data.sql
        mockMvc.perform(get("/api/restaurantes/2/produtos?page=0&size=2&sort=nome,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                // Valida a paginação
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                // Valida o conteúdo e a ordenação (Batata Frita vem antes de X-Bacon)
                .andExpect(jsonPath("$.data.content[0].nome").value("Batata Frita"));
    }
}