# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true

COPY src src
RUN mvn -B -q package -DskipTests

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /build/target/microservice-1.0-SNAPSHOT.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
