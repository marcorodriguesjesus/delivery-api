package com.deliverytech.delivery_api.security;

import com.deliverytech.delivery_api.BaseIntegrationTest;
import com.deliverytech.delivery_api.dto.LoginRequest;
import com.deliverytech.delivery_api.dto.ProdutoRequestDTO;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.enums.Role;
import com.deliverytech.delivery_api.repository.PedidoRepository; //
import com.deliverytech.delivery_api.repository.ProdutoRepository; //
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.repository.UsuarioRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityScenariosIT extends BaseIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    // Inject Repositories for dependent entities to clean them up first
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // 1. Clean up dependent data (Child tables) first to avoid Foreign Key violations
        pedidoRepository.deleteAll();   // Pedidos reference Clientes and Restaurantes
        produtoRepository.deleteAll();  // Produtos reference Restaurantes

        // 2. Clean up User data (Users reference Restaurantes if they are owners)
        usuarioRepository.deleteAll();

        // 3. Now safe to delete Parent tables
        restauranteRepository.deleteAll();

        // --- Scenario Setup ---

        // 1. Create a Restaurant
        Restaurante restaurante = new Restaurante();
        restaurante.setNome("Restaurante Security");
        restaurante.setTaxaEntrega(BigDecimal.ZERO);
        restaurante.setAtivo(true);
        restaurante = restauranteRepository.save(restaurante);

        // 2. Create Client User (Unique email for this test)
        Usuario cliente = new Usuario();
        cliente.setNome("Joao Security");
        cliente.setEmail("security.cliente@email.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setRole(Role.CLIENTE);
        cliente.setAtivo(true);
        cliente.setDataCriacao(LocalDateTime.now());
        usuarioRepository.save(cliente);

        // 3. Create Restaurant Owner User
        Usuario donoRestaurante = new Usuario();
        donoRestaurante.setNome("Dono Security");
        donoRestaurante.setEmail("security.restaurante@email.com");
        donoRestaurante.setSenha(passwordEncoder.encode("123456"));
        donoRestaurante.setRole(Role.RESTAURANTE);
        donoRestaurante.setRestauranteId(restaurante.getId());
        donoRestaurante.setAtivo(true);
        donoRestaurante.setDataCriacao(LocalDateTime.now());
        usuarioRepository.save(donoRestaurante);
    }

    private String obterToken(String email, String senha) throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setSenha(senha);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(response, "$.data.token");
    }

    @Test
    @DisplayName("Deve negar acesso a endpoint protegido sem token (403 Forbidden)")
    void testAcessoSemToken() throws Exception {
        mockMvc.perform(get("/api/pedidos/meus"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve permitir acesso com token válido (200 OK)")
    void testAcessoComTokenValido() throws Exception {
        String token = obterToken("security.cliente@email.com", "123456");

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Cliente não deve poder cadastrar produto (403 Forbidden)")
    void testAcessoNegadoPorRole() throws Exception {
        String tokenCliente = obterToken("security.cliente@email.com", "123456");

        ProdutoRequestDTO produto = new ProdutoRequestDTO();
        produto.setNome("Produto Hacker");
        produto.setPreco(new BigDecimal("10.00"));
        produto.setCategoria("Pizza");
        // Get ID of the restaurant created in setUp
        Long restauranteId = restauranteRepository.findAll().get(0).getId();
        produto.setRestauranteId(restauranteId);

        mockMvc.perform(post("/api/produtos")
                        .header("Authorization", "Bearer " + tokenCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Restaurante deve poder cadastrar produto (201 Created)")
    void testAcessoPermitidoPorRole() throws Exception {
        String tokenRestaurante = obterToken("security.restaurante@email.com", "123456");

        Usuario usuario = usuarioRepository.findByEmail("security.restaurante@email.com").orElseThrow();
        Long restauranteId = usuario.getRestauranteId();

        ProdutoRequestDTO produto = new ProdutoRequestDTO();
        produto.setNome("Pizza Especial");
        produto.setDescricao("Teste de segurança");
        produto.setPreco(new BigDecimal("50.00"));
        produto.setCategoria("Pizza");
        produto.setRestauranteId(restauranteId);

        mockMvc.perform(post("/api/produtos")
                        .header("Authorization", "Bearer " + tokenRestaurante)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve negar acesso com token inválido (403)")
    void testTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer token_invalido_12345"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve permitir acesso a endpoint público sem token (200 OK)")
    void testEndpointPublico() throws Exception {
        mockMvc.perform(get("/api/restaurantes"))
                .andExpect(status().isOk());
    }
}