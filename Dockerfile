# Build stage
FROM maven:3.8.1-openjdk-11 as builder

WORKDIR /app

# Copy entire project using direct wildcard (no ARG variable)
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy JAR from builder using direct path (no ARG variable)
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
