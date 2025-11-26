package com.deliverytech.delivery_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * ATIVIDADE 4.1: Classe base para Testes de Integração.
 *
 * @SpringBootTest: Carrega o contexto completo da aplicação Spring.
 * @AutoConfigureMockMvc: Configura e injeta o MockMvc.
 * @Transactional: Garante que cada teste rode em uma transação que será
 * revertida (rollback) ao final. Isso impede que um teste
 * suje o banco de dados (data.sql) para o próximo teste.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc; // Objeto principal para simular requisições HTTP

    @Autowired
    protected ObjectMapper objectMapper; // Usado para converter DTOs -> JSON e JSON -> DTOs
}