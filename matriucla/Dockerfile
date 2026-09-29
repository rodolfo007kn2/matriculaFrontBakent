# Etapa 1: Construccion (Compilar el codigo)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Damos permisos de ejecucion a maven
RUN chmod +x mvnw
# Descargamos dependencias
RUN ./mvnw dependency:go-offline
# Copiamos el codigo y compilamos saltando los tests para que sea rapido
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecucion (Correr el proyecto)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
