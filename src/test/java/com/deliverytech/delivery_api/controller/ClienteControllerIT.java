package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.ClienteResquestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClienteControllerIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Deve criar cliente com sucesso (201 Created)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCadastrarCliente_Success() throws Exception {
        ClienteResquestDTO request = new ClienteResquestDTO();
        request.setNome("Novo Cliente");
        request.setEmail("novo.cliente@teste.com");
        request.setTelefone("11999998888");
        request.setEndereco("Rua Teste, 100");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("novo.cliente@teste.com"));
    }

    @Test
    @DisplayName("Deve retornar Erro 400 ao tentar cadastrar dados inválidos")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCadastrarCliente_Invalid() throws Exception {
        ClienteResquestDTO request = new ClienteResquestDTO();
        request.setNome(""); // Inválido
        request.setEmail("email-invalido"); // Inválido

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.status").value(400));
    }

    @Test
    @DisplayName("Deve buscar cliente existente por ID (200 OK)")
    @WithMockUser(roles = {"ADMIN"})
    void testBuscarCliente_Success() throws Exception {
        // O ID 1 já existe no data.sql (carregado no H2)
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso (200 OK)")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAtualizarCliente() throws Exception {
        ClienteResquestDTO updateRequest = new ClienteResquestDTO();
        updateRequest.setNome("João Silva Atualizado");
        updateRequest.setEmail("joao@email.com"); // Mantém o mesmo email
        updateRequest.setTelefone("11988887777");
        updateRequest.setEndereco("Nova Rua, 200");

        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nome").value("João Silva Atualizado"))
                .andExpect(jsonPath("$.data.endereco").value("Nova Rua, 200"));
    }
}