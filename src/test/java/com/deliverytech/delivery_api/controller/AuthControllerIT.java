package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.LoginRequest;
import com.deliverytech.delivery_api.dto.RegisterRequest;
import com.deliverytech.delivery_api.enums.Role;
import com.deliverytech.delivery_api.repository.UsuarioRepository; // Importar
import org.junit.jupiter.api.BeforeEach; // Importar
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired; // Importar
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIT extends BaseIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Limpar o banco antes de cada teste para evitar conflito de email único
    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

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

    @Test
    @DisplayName("Deve realizar login com credenciais válidas e retornar token (200 OK)")
    void testLogin_Success() throws Exception {
        // 1. PRIMEIRO: Cria o usuário via API de registro (garante hash correta)
        RegisterRequest registro = new RegisterRequest();
        registro.setNome("Joao Login");
        registro.setEmail("joao.login@email.com");
        registro.setSenha("123456");
        registro.setRole(Role.CLIENTE);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated());

        // 2. DEPOIS: Tenta logar com ele
        LoginRequest login = new LoginRequest();
        login.setEmail("joao.login@email.com");
        login.setSenha("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("joao.login@email.com"));
    }

    @Test
    @DisplayName("Deve falhar login com senha incorreta (401 ou 400)")
    void testLogin_InvalidCredentials() throws Exception {
        // Cria usuário válido
        RegisterRequest registro = new RegisterRequest();
        registro.setNome("Joao Erro");
        registro.setEmail("joao.erro@email.com");
        registro.setSenha("123456");
        registro.setRole(Role.CLIENTE);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated());

        // Tenta logar com senha errada
        LoginRequest login = new LoginRequest();
        login.setEmail("joao.erro@email.com");
        login.setSenha("senhaerrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                // Seu AuthController lança BusinessException, que o Handler mapeia para 400 Bad Request
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}