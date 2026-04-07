# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy the Gradle executable and wrapper files
COPY gradlew .
COPY gradle gradle

# Copy the build configuration files
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src src

# Make the wrapper executable and build the project, skipping tests
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# Stage 2: Create the production image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the default port (Render will assign a dynamic port via the PORT environment variable)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
