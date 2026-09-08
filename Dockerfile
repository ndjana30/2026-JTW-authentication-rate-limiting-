# Multi-stage Dockerfile for building and running the Spring Boot application on Render
# Build stage - use Maven with Java 21
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy wrapper and pom first to leverage Docker layer caching
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw

# Copy only source and resources, then build
COPY src ./src

# Use the wrapper to build the executable jar (skip tests for faster builds)
RUN ./mvnw -B -DskipTests package

# Runtime stage - use a lightweight Java 21 JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the packaged jar from the build stage
COPY --from=build /workspace/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Allow Render to set the port via the PORT environment variable; provide reasonable JVM defaults
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Start the application; Render provides PORT env var, default to 8080 if not set
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app.jar"]
