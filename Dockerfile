# --- Estágio 1: Construção (Build) ---
# Usamos uma imagem que ja tem o Maven instalado para evitar erros com o script mvnw no Windows
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia apenas o arquivo de dependencias primeiro para aproveitar o cache do Docker
COPY pom.xml .
# Baixa as dependencias (isso fará o build ser mais rápido nas próximas vezes)
RUN mvn dependency:go-offline

# Copia o codigo fonte
COPY src ./src

# Compila o projeto e gera o .jar (pula os testes para ser mais rápido)
RUN mvn clean package -DskipTests

# --- Estágio 2: Execucao ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Pega o .jar gerado no estagio anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]