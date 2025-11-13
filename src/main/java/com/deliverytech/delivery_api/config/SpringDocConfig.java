package com.deliverytech.delivery_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ATIVIDADE 2.1 e 2.4: Configuração personalizada do SpringDoc/OpenAPI
 */
@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 2.4: Informações da API (título, versão, descrição)
                .info(new Info()
                        .title("DeliveryTech API")
                        .version("v1.0.0")
                        .description("API robusta para gerenciamento de pedidos, restaurantes e clientes da plataforma DeliveryTech.")

                        // 2.4: Contato e licença
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento DeliveryTech")
                                .email("dev@deliverytech.com")
                                .url("https://deliverytech.com/contato"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                // 2.4: Servidores (dev, prod)
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de Desenvolvimento Local"),
                        new Server().url("https://api.deliverytech.com").description("Servidor de Produção")
                ));
    }
}