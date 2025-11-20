package com.deliverytech.delivery_api.security;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.LoginRequest;
import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityScenariosIT extends BaseIntegrationTest {

    // Auxiliar para pegar token de um usuário específico
    private String obterToken(String email, String senha) throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setSenha(senha);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(response, "$.data.token");
    }

    // CENÁRIO 4: ACESSO A ENDPOINT PROTEGIDO SEM TOKEN
    @Test
    @DisplayName("Deve negar acesso a endpoint protegido sem token (403 Forbidden)")
    void testAcessoSemToken() throws Exception {
        mockMvc.perform(get("/api/pedidos/meus"))
                .andExpect(status().isForbidden()); // Spring Security retorna 403 por padrão para anonimo
    }

    // CENÁRIO 5: ACESSO A ENDPOINT PROTEGIDO COM TOKEN VÁLIDO
    @Test
    @DisplayName("Deve permitir acesso com token válido (200 OK)")
    void testAcessoComTokenValido() throws Exception {
        String token = obterToken("joao@email.com", "123456"); // Cliente

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // CENÁRIO 6: ACESSO NEGADO POR ROLE (Cliente tentando cadastrar produto)
    @Test
    @DisplayName("Cliente não deve poder cadastrar produto (403 Forbidden)")
    void testAcessoNegadoPorRole() throws Exception {
        String tokenCliente = obterToken("joao@email.com", "123456");

        ProdutoRequestDTO produto = new ProdutoRequestDTO();
        produto.setNome("Produto Hacker");
        produto.setPreco(new BigDecimal("10.00"));
        produto.setCategoria("Pizza");
        produto.setRestauranteId(1L);

        mockMvc.perform(post("/api/produtos")
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isForbidden()); // Role CLIENTE não tem permissão
    }

    // CENÁRIO 7: ACESSO PERMITIDO POR ROLE (Restaurante cadastrando produto)
    @Test
    @DisplayName("Restaurante deve poder cadastrar produto (201 Created)")
    void testAcessoPermitidoPorRole() throws Exception {
        // pizza@palace.com é RESTAURANTE e dono do restaurante ID 1
        String tokenRestaurante = obterToken("pizza@palace.com", "123456");

        ProdutoRequestDTO produto = new ProdutoRequestDTO();
        produto.setNome("Pizza Especial");
        produto.setDescricao("Nova Pizza teste de segurança");
        produto.setPreco(new BigDecimal("50.00"));
        produto.setCategoria("Pizza");
        produto.setRestauranteId(1L); // ID do próprio restaurante

        mockMvc.perform(post("/api/produtos")
                        .header("Authorization", "Bearer " + tokenRestaurante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated());
    }

    // CENÁRIO 8: TOKEN EXPIRADO / INVÁLIDO
    @Test
    @DisplayName("Deve negar acesso com token inválido (403/401)")
    void testTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer token_invalido_12345"))
                .andExpect(status().isForbidden()); // Ou 401 dependendo da config do EntryPoint
    }

    // CENÁRIO 9: ENDPOINT PÚBLICO ACESSÍVEL
    @Test
    @DisplayName("Deve permitir acesso a endpoint público sem token (200 OK)")
    void testEndpointPublico() throws Exception {
        mockMvc.perform(get("/api/restaurantes")) // Listar restaurantes é público
                .andExpect(status().isOk());
    }

    // CENÁRIO 10: VERIFICAÇÃO DE PROPRIEDADE (Restaurante A mexendo em Restaurante B)
    @Test
    @DisplayName("Restaurante não deve alterar produto de outro restaurante (403 Forbidden)")
    void testVerificacaoPropriedade() throws Exception {
        // Login como Restaurante 1 (pizza@palace.com)
        String tokenRestaurante1 = obterToken("pizza@palace.com", "123456");

        // Tenta deletar Produto 4 (que pertence ao Restaurante 2 - Burger House)
        // O endpoint DELETE /api/produtos/{id} tem validação @PreAuthorize("... isOwner(#id)")
        mockMvc.perform(delete("/api/produtos/4")
                        .header("Authorization", "Bearer " + tokenRestaurante1))
                .andExpect(status().isForbidden());
    }
}