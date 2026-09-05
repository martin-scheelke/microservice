# Time microservice

## Description

A Maven multi-module project:

- `microservice` — returns the current time, served by a switchable jOOQ or
  JPA/ORM data-access layer (`app.db.access-mode`: `jooq` default, or `orm`),
  backed by PostgreSQL with Flyway migrations.
- `client` (`time-client`) — a small Spring Boot consumer that calls
  `GET /api/v1/time` and re-exposes it at `GET /client/time`.

The two are tested against each other with Pact V4 consumer-driven contracts:
`client`'s `TimeClientPactTest` defines the contract against Pact's own
in-process mock server and writes it to `microservice/pacts/`; `microservice`'s
`TimeApiPactVerificationTest` then replays it against the real running app.

## Prerequisites

- Java 24
- Maven
- Docker — required for Testcontainers-backed tests, `docker compose`, and
  image builds
- `kind` and `kubectl` — only for the local Kubernetes deploy
- `gitlab-runner` — only for running `.gitlab-ci.yml` locally

## Build

```
mvn package
```

## Test

```
mvn test                              # whole reactor: unit + integration (needs Docker)
mvn -pl client -am test               # client only: unit + consumer Pact test (no Docker needed)
mvn -pl microservice -am test -Dtest=TimeApiPactVerificationTest   # provider Pact verification (needs Docker)
```

Run the consumer Pact test before the provider verification test, since the
latter reads the contract file the former writes.

## Run

With Docker Compose (Postgres + both apps):

```
docker compose up --build
curl http://localhost:8080/api/v1/time
curl http://localhost:8081/client/time
```

Against a local Postgres (`timedb`/`timedb`/`timedb`, matching
`microservice/src/main/resources/application.yml`), without Compose:

```
mvn -pl microservice -am spring-boot:run
mvn -pl client -am spring-boot:run
```

On a local kind cluster:

```
./scripts/kind-up.ps1     # builds both images, creates/reuses the cluster, deploys via Kustomize
./scripts/kind-down.ps1   # tears it down
```
