package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.ItemPedidoDTO;
import com.deliverytech.delivery_api.dto.PedidoRequestDTO;
import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PedidoControllerIT extends BaseIntegrationTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("Deve criar pedido completo com sucesso (201 Created)")
    // Simula um cliente logado (ID 1 do data.sql é o João Silva, email: joao@email.com)
    @WithMockUser(username = "joao@email.com", roles = {"CLIENTE"})
    void testCriarPedido_Success() throws Exception {
        // Cliente 1, Restaurante 1, Produtos 1 e 2 (do data.sql)
        ItemPedidoDTO item1 = new ItemPedidoDTO(); item1.setProdutoId(1L); item1.setQuantidade(1);
        ItemPedidoDTO item2 = new ItemPedidoDTO(); item2.setProdutoId(2L); item2.setQuantidade(1);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setClienteId(1L);
        request.setRestauranteId(1L);
        request.setItens(List.of(item1, item2));

        // Valor esperado: (35.90 * 1) + (38.90 * 1) + 5.00 (taxa) = 79.80
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.valorTotal").value(79.80))
                .andExpect(jsonPath("$.data.status").value("PENDENTE"));
    }

    @Test
    @DisplayName("Deve retornar 400 se produto não pertencer ao restaurante")
    @WithMockUser(username = "joao@email.com", roles = {"CLIENTE"})
    void testCriarPedido_ProdutoErrado() throws Exception {
        // Restaurante 1, mas Produto 4 (que é do Restaurante 2 no data.sql)
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(4L);
        item.setQuantidade(1);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setClienteId(1L);
        request.setRestauranteId(1L);
        request.setItens(List.of(item));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // BusinessException
                .andExpect(jsonPath("$.error.message").exists());
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar pedir produto indisponível (Cenário de Estoque)")
    @WithMockUser(username = "joao@email.com", roles = {"CLIENTE"})
    void testCriarPedido_ProdutoIndisponivel() throws Exception {
        // ARRANGE: Indisponibilizar o Produto 1 no banco H2
        Produto produto = produtoRepository.findById(1L).orElseThrow();
        produto.setDisponivel(false);
        produtoRepository.save(produto);

        // Preparar Request
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProdutoId(1L); // Produto que acabamos de "esgotar"
        item.setQuantidade(1);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setClienteId(1L);
        request.setRestauranteId(1L);
        request.setItens(List.of(item));

        // ACT & ASSERT
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Produto indisponível: " + produto.getNome()));

        // Cleanup (opcional, pois o teste usa @Transactional e faz rollback, mas bom garantir)
        produto.setDisponivel(true);
        produtoRepository.save(produto);
    }

    @Test
    @DisplayName("Deve atualizar status do pedido (200 OK)")
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Apenas Admin ou Restaurante pode atualizar
    void testAtualizarStatus() throws Exception {
        // Pedido 1 já existe no data.sql como PENDENTE
        mockMvc.perform(patch("/api/pedidos/1/status")
                        .param("status", StatusPedido.PREPARANDO.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PREPARANDO"));
    }

    @Test
    @DisplayName("Deve listar histórico de pedidos do cliente")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListarHistoricoCliente() throws Exception {
        mockMvc.perform(get("/api/pedidos/cliente/1?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").isNotEmpty());
    }
}