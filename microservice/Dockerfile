# syntax=docker/dockerfile:1
# Build context is the repo root, since this is one module of a Maven reactor.
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /build

COPY pom.xml .
COPY microservice/pom.xml microservice/pom.xml
COPY client/pom.xml client/pom.xml
RUN mvn -B -q -pl microservice -am dependency:go-offline || true

COPY microservice/src microservice/src
RUN mvn -B -q -pl microservice -am package -DskipTests

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /build/microservice/target/microservice-1.0-SNAPSHOT.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
