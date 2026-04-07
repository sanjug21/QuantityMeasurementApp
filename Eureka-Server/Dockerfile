# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# Copy gradle wrapper and configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Grant execution rights to the wrapper
RUN chmod +x gradlew

# Download dependencies (optional step to cache dependencies)
RUN ./gradlew dependencies --no-daemon

# Copy the application source code
COPY src src

# Build the application (skipping tests for faster deployment)
RUN ./gradlew bootJar --no-daemon

# Stage 2: Create the final lightweight image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the executable jar from the builder stage
# Spring Boot's bootJar task creates the executable jar in build/libs/
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the default Eureka Server port (Render will use its own PORT env variable)
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
