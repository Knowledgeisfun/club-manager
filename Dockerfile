FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .

# FIX: Give execution permission to the Maven wrapper
RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]