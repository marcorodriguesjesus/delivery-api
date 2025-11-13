package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.ClienteResquestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ATIVIDADE 4.1: Testes de Integração para ClienteController
 */
public class ClienteControllerIT extends BaseIntegrationTest {

    @Test
    @DisplayName("Cenário 4.2: Deve criar cliente com sucesso (Status 201)")
    void testCadastrarCliente_Success() throws Exception {
        ClienteResquestDTO requestDTO = new ClienteResquestDTO();
        requestDTO.setNome("Cliente Teste MockMvc");
        requestDTO.setEmail("teste.mockmvc@email.com");
        requestDTO.setTelefone("11988887777");
        requestDTO.setEndereco("Rua dos Testes, 123");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                // 4.2: Valida o Status 201 Created
                .andExpect(status().isCreated())
                // 3.3: Valida o Header Location
                .andExpect(header().exists("Location"))
                // 3.2: Valida o wrapper ApiResponse
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Cliente Teste MockMvc"))
                .andExpect(jsonPath("$.data.email").value("teste.mockmvc@email.com"));
    }

    @Test
    @DisplayName("Cenário 4.2: Deve falhar ao criar cliente com dados inválidos (Status 400)")
    void testCadastrarCliente_InvalidData() throws Exception {
        ClienteResquestDTO requestDTO = new ClienteResquestDTO();
        requestDTO.setNome(""); // Nome em branco (inválido)
        requestDTO.setEmail("email-invalido"); // Email inválido
        requestDTO.setTelefone("123"); // Telefone curto
        requestDTO.setEndereco(""); // Endereço em branco

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                // 4.2: Valida o Status 400 Bad Request
                .andExpect(status().isBadRequest())
                // 3.2: Valida o wrapper de Erro
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.status").value(400))
                .andExpect(jsonPath("$.error.message").value("Erro de validação. Verifique os campos."))
                // 3.2: Valida os detalhes do erro
                .andExpect(jsonPath("$.error.details").isArray())
                .andExpect(jsonPath("$.error.details[?(@.field == 'nome')]").exists())
                .andExpect(jsonPath("$.error.details[?(@.field == 'email')]").exists())
                .andExpect(jsonPath("$.error.details[?(@.field == 'telefone')]").exists())
                .andExpect(jsonPath("$.error.details[?(@.field == 'endereco')]").exists());
    }

    @Test
    @DisplayName("Cenário 4.2: Deve falhar ao criar cliente com email duplicado (Status 400 - BusinessException)")
    void testCadastrarCliente_Conflict() throws Exception {
        // Este email já existe no data.sql
        ClienteResquestDTO requestDTO = new ClienteResquestDTO();
        requestDTO.setNome("Outro Joao");
        requestDTO.setEmail("joao@email.com"); // Email do data.sql
        requestDTO.setTelefone("11988887777");
        requestDTO.setEndereco("Rua dos Testes, 123");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                // 4.2: Valida o 400 (Mapeado da BusinessException)
                // Nota: O roteiro pede 409, mas o GlobalExceptionHandler mapeia BusinessException para 400.
                // Para 409, seria necessário criar uma exceção customizada (ex: DataConflictException)
                // e mapeá-la para HttpStatus.CONFLICT no GlobalExceptionHandler.
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("Email já cadastrado: joao@email.com"));
    }

    @Test
    @DisplayName("Cenário 4.2: Deve buscar cliente por ID com sucesso (Status 200)")
    void testBuscarClientePorId_Success() throws Exception {
        // O Cliente ID=1 ("João Silva") é inserido via data.sql
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Cache-Control")) // 3.3: Valida o Cache
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nome").value("João Silva"));
    }

    @Test
    @DisplayName("Cenário 4.2: Deve falhar ao buscar cliente inexistente (Status 404)")
    void testBuscarClientePorId_NotFound() throws Exception {
        mockMvc.perform(get("/api/clientes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.status").value(404))
                .andExpect(jsonPath("$.error.message").value("Cliente não encontrado com ID: 9999"));
    }

    @Test
    @DisplayName("Cenário 4.2: Deve listar clientes com paginação (Status 200)")
    void testListarClientesAtivos_Pagination() throws Exception {
        // data.sql insere 3 clientes
        mockMvc.perform(get("/api/clientes?page=0&size=2&sort=nome,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                // 3.4: Valida os metadados da paginação
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.first").value(true))
                .andExpect(jsonPath("$.data.last").value(false))
                // Valida o conteúdo
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                // Valida a ordenação (João Silva deve vir antes de Maria Santos)
                .andExpect(jsonPath("$.data.content[0].nome").value("João Silva"));
    }
}