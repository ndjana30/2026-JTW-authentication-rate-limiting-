# Multi-stage Dockerfile for building and running the Java application

# Build stage using Maven and OpenJDK 11
FROM maven:3.8.1-openjdk-11 AS build
WORKDIR /build

# Copy only the pom first to leverage Docker layer caching for dependencies
COPY pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime stage using Eclipse Temurin (Adoptium) JRE 11
FROM eclipse-temurin:11-jre
WORKDIR /app

# Copy the built JAR from the build stage. Adjust the glob if your artifact name differs.
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
