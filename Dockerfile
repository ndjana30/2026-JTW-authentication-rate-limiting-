# Build stage using Maven with JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml ./
RUN mvn -B -f pom.xml dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

# Runtime stage using JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
