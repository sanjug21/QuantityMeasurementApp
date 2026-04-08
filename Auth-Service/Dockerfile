# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Copy Gradle wrapper and build configuration files first for better layer caching.
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Wrapper can lose execute bit on Windows checkout.
RUN chmod +x gradlew

# Warm dependency cache before copying sources.
RUN ./gradlew dependencies --no-daemon

# Copy source and create the executable Spring Boot jar.
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# Stage 2: Create the final image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]