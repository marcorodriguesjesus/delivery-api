# DeliveryAPI

Sistema de delivery desenvolvido com Spring Boot & Java 21.

## 🚀 Tecnologias
- **Java 21 LTS**
- Spring Boot 3.3.6
- Spring Web
- Spring Data JPA
- MySQL (produção)
- H2 Database (Testes)
- Maven
- **SpringDoc OpenAPI (Swagger UI)**
- **JUnit 5 & Mockito (Testes)**

## ⚡ Recursos Modernos Utilizados
- Records (Java 14+)
- Text Blocks (Java 15+)
- Pattern Matching (Java 17+)
- Virtual Threads (Java 21)

## 🏃‍♂️ Como Executar
1. **Requisitos:** JDK 21 instalado
2. Clone o repositório
3. Configure o banco de dados no `application.properties` (se necessário)
4. Execute: `./mvnw spring-boot:run`
5. **Acessar Documentação:** http://localhost:8080/swagger-ui.html

## 🧪 Como Rodar Os Testes
```bash
./mvnw test
```

## 📖 Documentação da API (Swagger)
A API está totalmente documentada utilizando OpenAPI 3.0.
Acesse a interface interativa em: **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### 🔑 Como Autenticar
Alguns endpoints requerem um Token JWT. Siga os passos abaixo para testar:
1. Vá até `POST /api/auth/login` no Swagger.
2. Utilize as credenciais padrão de admin:
    - **Email:** `admin@delivery.com`
    - **Senha:** `123456`
3. Copie o `token` retornado na resposta (sem as aspas).
4. Clique no botão **Authorize** 🔓 no topo da página.
5. Cole o token no campo "Value" e clique em **Authorize**.

## 📋 Principais Endpoints
- **Auth:** Login e Registro de Usuários (`/api/auth`)
- **Restaurantes:** Gerenciamento de restaurantes (`/api/restaurantes`)
- **Produtos:** Gerenciamento do cardápio (`/api/produtos`)
- **Pedidos:** Ciclo de vida completo do pedido (`/api/pedidos`)
- **Relatórios:** Métricas de vendas e desempenho (`/api/relatorios`)

## 🔧 Configuração
- Porta: 8080
- Banco de Dados: MySQL
- Perfil de Desenvolvedor: MySQL (Docker ou Local)
- Perfil de Testes: H2

## 👨‍💻 Desenvolvedor
[Marco Jesus]

###### Desenvolvido com JDK 21 & Spring Boot 3.3.6
