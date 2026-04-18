# Stage 1: Build
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

COPY . .
RUN mvn clean package -pl api-gateway -DskipTests

# Stage 2: Runtime (Using Temurin as OpenJDK is deprecated)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/api-gateway/target/*.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]