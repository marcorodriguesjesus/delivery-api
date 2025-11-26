package com.deliverytech.delivery_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DeliveryTech API")
                        .version("v1.0.0")
                        .description("API robusta para gerenciamento de pedidos, restaurantes e clientes da plataforma DeliveryTech.")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento DeliveryTech")
                                .email("dev@deliverytech.com")
                                .url("https://deliverytech.com/contato"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de Desenvolvimento Local"),
                        new Server().url("https://api.deliverytech.com").description("Servidor de Produção")
                ))
                // CONFIGURAÇÃO DE SEGURANÇA JWT
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT aqui para autenticar as requisições.")
                        ));
    }
}