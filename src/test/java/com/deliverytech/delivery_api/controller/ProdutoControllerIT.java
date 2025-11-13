package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ATIVIDADE 4.1: Testes de Integração para ProdutoController (CRUD Completo)
 */
public class ProdutoControllerIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Cenário 4.1: Deve criar, buscar, atualizar e deletar um produto (CRUD)")
    void testProdutoCRUD() throws Exception {

        // --- 1. CREATE (POST) ---
        ProdutoRequestDTO createDto = new ProdutoRequestDTO();
        createDto.setNome("Suco de Laranja Teste");
        createDto.setPreco(new BigDecimal("10.00"));
        createDto.setCategoria("Bebida");
        createDto.setRestauranteId(1L); // Pizzaria Bella (do data.sql)

        String responseJson = mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Suco de Laranja Teste"))
                .andReturn().getResponse().getContentAsString();

        // Extrai o ID do produto criado
        Integer produtoId = com.jayway.jsonpath.JsonPath.read(responseJson, "$.data.id");

        // --- 2. READ (GET) ---
        mockMvc.perform(get("/api/produtos/" + produtoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(produtoId))
                .andExpect(jsonPath("$.data.nome").value("Suco de Laranja Teste"));

        // --- 3. UPDATE (PUT) ---
        ProdutoRequestDTO updateDto = new ProdutoRequestDTO();
        updateDto.setNome("Suco de Laranja 1L");
        updateDto.setPreco(new BigDecimal("15.00"));
        updateDto.setCategoria("Bebida");
        updateDto.setRestauranteId(1L);

        mockMvc.perform(put("/api/produtos/" + produtoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Suco de Laranja 1L"))
                .andExpect(jsonPath("$.data.preco").value(15.00));

        // --- 4. DELETE (DELETE) ---
        mockMvc.perform(delete("/api/produtos/" + produtoId))
                .andExpect(status().isNoContent()); // 4.2: Valida 204 No Content

        // --- 5. VERIFY (GET) ---
        mockMvc.perform(get("/api/produtos/" + produtoId))
                .andExpect(status().isNotFound()); // 4.2: Valida 404
    }
}