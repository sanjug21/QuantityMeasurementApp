# Stage 1: Build the application
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]