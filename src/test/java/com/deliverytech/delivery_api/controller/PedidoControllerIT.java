package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.ItemPedidoDTO;
import com.deliverytech.delivery_api.dto.PedidoRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ATIVIDADE 4.1: Testes de Integração para PedidoController (Cenários Complexos)
 */
public class PedidoControllerIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Cenário 4.1: Deve criar um pedido complexo com sucesso (Status 201)")
    void testCriarPedido_Success() throws Exception {
        // Usando dados do data.sql:
        // Cliente 1 (João Silva)
        // Restaurante 2 (Burger House)
        // Produtos 4 (X-Burger) e 6 (Batata Frita)

        ItemPedidoDTO item1 = new ItemPedidoDTO();
        item1.setProdutoId(4L); // X-Burger
        item1.setQuantidade(1);

        ItemPedidoDTO item2 = new ItemPedidoDTO();
        item2.setProdutoId(6L); // Batata Frita
        item2.setQuantidade(2);

        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setRestauranteId(2L);
        requestDTO.setItens(List.of(item1, item2));
        requestDTO.setObservacoes("Teste MockMvc");

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cliente.id").value(1L))
                .andExpect(jsonPath("$.data.restaurante.id").value(2L))
                // Valida o cálculo: (1 * 18.90) + (2 * 12.90) + 3.50 (taxa) = 18.90 + 25.80 + 3.50 = 48.20
                .andExpect(jsonPath("$.data.valorTotal").value(48.20));
    }

    @Test
    @DisplayName("Cenário 4.2: Deve falhar ao criar pedido com cliente inexistente (Status 404)")
    void testCriarPedido_ClienteNotFound() throws Exception {
        ItemPedidoDTO item1 = new ItemPedidoDTO();
        item1.setProdutoId(4L);
        item1.setQuantidade(1);

        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setClienteId(9999L); // ID Inexistente
        requestDTO.setRestauranteId(2L);
        requestDTO.setItens(List.of(item1));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("Cliente não encontrado: 9999"));
    }

    @Test
    @DisplayName("Cenário 4.1: Deve falhar ao criar pedido com produto de outro restaurante (Status 400)")
    void testCriarPedido_ProdutoNaoPertenceRestaurante() throws Exception {
        // Usando dados do data.sql:
        // Restaurante 1 (Pizzaria Bella)
        // Produto 4 (X-Burger, que é do Restaurante 2)

        ItemPedidoDTO item1 = new ItemPedidoDTO();
        item1.setProdutoId(4L); // X-Burger (do Restaurante 2)
        item1.setQuantidade(1);

        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setClienteId(1L);
        requestDTO.setRestauranteId(1L); // Pizzaria Bella (Restaurante 1)
        requestDTO.setItens(List.of(item1));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest()) // BusinessException
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("Produto X-Burger não pertence ao restaurante selecionado."));
    }

    @Test
    @DisplayName("Cenário 4.2: Deve cancelar pedido com sucesso (Status 204)")
    void testCancelarPedido_Success() throws Exception {
        // O Pedido ID=1 (PED1234567890) é PENDENTE no data.sql
        mockMvc.perform(delete("/api/pedidos/1"))
                .andExpect(status().isNoContent()); // 3.1: 204 No Content
    }
}