# Stage 1: Build the application using Maven
FROM maven:3.8.8-eclipse-temurin-21 AS build
WORKDIR /app

# Download dependencies first (for better build caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build the application
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create the final lightweight runtime image
FROM amazoncorretto:21-alpine
WORKDIR /app

# Install curl (needed for ECS health checks)
RUN apk add --no-cache curl

# Copy the fat jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
