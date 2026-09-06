# syntax=docker/dockerfile:1
# Build context is the repo root, since this is one module of a Maven reactor.
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /build

COPY pom.xml .
COPY microservice/pom.xml microservice/pom.xml
COPY client/pom.xml client/pom.xml
RUN mvn -B -pl client -am dependency:go-offline

COPY client/src client/src
RUN mvn -B -pl client -am package -DskipTests

FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /build/client/target/time-client-1.0-SNAPSHOT.jar app.jar
USER app

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
