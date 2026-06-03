# === PASO 1: Compilar la aplicación con Maven ===
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos los archivos de configuración y dependencias desde donde esté parado el Root Directory
COPY pom.xml .
COPY src ./src

# Compilamos la aplicación salteándonos los tests para cuidar la RAM de Render
RUN mvn clean package -DskipTests

# === PASO 2: Crear el contenedor final liviano para correr el JAR ===
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el .jar generado buscando de forma elástica en la carpeta target
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto estándar
EXPOSE 8080

# Comando para ejecutar la aplicación de Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
