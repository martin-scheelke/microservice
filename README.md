# Time microservice

Returns the current time. Data access is served either by jOOQ or JPA/ORM,
selected at runtime via `app.db.access-mode` (`jooq` default, or `orm`).
Backed by PostgreSQL, with schema managed by Flyway.

This repo also contains `client/`, a small Spring Boot consumer app used to
demonstrate consumer-driven contract testing against this microservice with
Pact V4.

## Modules

- `/` (`microservice`) — the time API (jOOQ/JPA switchable), Postgres, Flyway.
- `client/` (`time-client`) — calls `GET /api/v1/time` and re-exposes it at
  `GET /client/time`.

## Running locally

### With Docker Compose (Postgres + both apps)

```
docker compose up --build
curl http://localhost:8080/api/v1/time
curl http://localhost:8081/client/time
```

### Against a local Postgres, without Docker Compose

Start a Postgres 16 instance with a `timedb` database/user/password of
`timedb`/`timedb`/`timedb` (matching `src/main/resources/application.yml`),
then:

```
mvn spring-boot:run
mvn -f client/pom.xml spring-boot:run
```

## Tests

```
mvn test                                    # unit + integration (needs Docker for Testcontainers)
mvn -f client/pom.xml test                  # unit + consumer pact test (no Docker needed)
```

Integration tests (`*IntegrationTest`, `*RestAssuredTest`, `*DumpTest`) and the
provider pact verification test start a real PostgreSQL container via
Testcontainers (`org.test.support.PostgresTestcontainersConfig`), so a running
Docker engine is required for those. Pure unit/slice tests
(`DefaultTimeServiceTest`, `TimeControllerTest`, `ClientControllerTest`) do not
need Docker.

## Contract testing with Pact V4

The consumer (`client`) and provider (`microservice`) are tested against a
shared contract file instead of running against each other directly:

1. `TimeClientPactTest` (in `client`) defines the expected interaction and
   runs it against **Pact's own local mock HTTP server** — Pact's JUnit 5
   extension starts and tears this down automatically for each test, so
   nothing needs to be run separately. This produces the contract file at
   `pacts/time-client-time-microservice.json` (shared path, configured via
   `pact.rootDir` in `client/pom.xml`).

2. `TimeApiPactVerificationTest` (in `microservice`) reads that same contract
   file (`@PactFolder("pacts")`) and replays each recorded interaction against
   the **real, running microservice** (a full `@SpringBootTest` with a real
   Postgres Testcontainer behind it) — no mock server here, since the
   provider itself is what's under test.

Run them in order:

```
mvn -f client/pom.xml test -Dtest=TimeClientPactTest
mvn test -Dtest=TimeApiPactVerificationTest
```

## Local Kubernetes (kind + kubectl + Kustomize)

Requires `docker`, `kind`, and `kubectl` on PATH.

```
./scripts/kind-up.ps1     # builds both images, creates/reuses the kind cluster, applies k8s/overlays/local
./scripts/kind-down.ps1   # tears the cluster down
```

Once up:

```
curl http://localhost:30080/api/v1/time    # microservice, via NodePort
curl http://localhost:30081/client/time    # time-client, via NodePort
```

Manifests live under `k8s/base` (Postgres, microservice, time-client) with a
`k8s/overlays/local` Kustomize overlay that namespaces everything under
`time-microservice-local`.

## GitLab CI/CD

`.gitlab-ci.yml` defines: build → unit/integration test (Testcontainers via
Docker-in-Docker) → Pact consumer/provider contract stage → Docker image
build. To run it against a local GitLab instance, register a local
`gitlab-runner` with the Docker executor and docker-in-docker enabled. To try
a single job without a full GitLab server:

```
gitlab-runner exec docker unit-test-microservice
```

## Known local-environment limitation

This branch was authored and self-tested in a sandboxed Windows environment
without working nested virtualization, so Docker Desktop's Linux engine
(`wsl -l -v` shows `docker-desktop` stuck `Stopped`) could not actually start
here. As a result:

- Compilation and Docker-free tests (unit tests, the consumer Pact test) were
  run and pass.
- Testcontainers-backed integration tests, the provider Pact verification
  test, `docker build`/`docker compose`, and the kind/kubectl scripts compile
  and read correctly but could **not** be executed in this environment — they
  need to be run on a machine with a working Docker engine before merging.
