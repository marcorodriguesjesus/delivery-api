package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.LoginRequest;
import com.deliverytech.delivery_api.dto.RegisterRequest;
import com.deliverytech.delivery_api.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIT extends BaseIntegrationTest {

    // CENÁRIO 1: REGISTRO DE USUÁRIO
    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso (201 Created)")
    void testRegistrarUsuario_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Novo Usuario Teste");
        request.setEmail("novo.teste@email.com");
        request.setSenha("123456");
        request.setRole(Role.CLIENTE);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("novo.teste@email.com"));
    }

    // CENÁRIO 2: LOGIN VÁLIDO
    @Test
    @DisplayName("Deve realizar login com credenciais válidas e retornar token (200 OK)")
    void testLogin_Success() throws Exception {
        // Usuário criado no data.sql (joao@email.com / 123456)
        LoginRequest login = new LoginRequest();
        login.setEmail("joao@email.com");
        login.setSenha("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty()) // Token deve estar presente
                .andExpect(jsonPath("$.data.user.email").value("joao@email.com"));
    }

    // CENÁRIO 3: LOGIN INVÁLIDO
    @Test
    @DisplayName("Deve falhar login com senha incorreta (401/403 ou 400)")
    void testLogin_InvalidCredentials() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("joao@email.com");
        login.setSenha("senhaerrada"); // Senha errada

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                // Dependendo de como sua ExceptionHandler trata BadCredentialsException, pode ser 400 ou 401
                // Baseado no seu AuthController, ele lança BusinessException que vira 400 Bad Request
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}