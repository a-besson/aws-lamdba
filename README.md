# AWS Lambda — Quarkus & Spring Boot samples

Sibling AWS Lambda samples built with Quarkus and with Spring Boot, showing functionally
equivalent implementations of the same two Lambda shapes (plain Lambda behind a Function URL, and
Lambda behind API Gateway REST) on each stack. Each sample ships its Terraform stack.

## Prerequisites

```bash
task setup    # Brewfile (Maven, Temurin 21, Terraform, TFLint, Trivy, Podman...), git hooks, TFLint plugins
task doctor   # check the tools and Java 21; an AWS session is only needed to deploy
```

## Samples

| Sample | Stack | Type | Runtime / package | Docs |
|---|---|---|---|---|
| `quarkus-lambda` | Quarkus | Lambda + Function URL | `provided.al2023`, native zip | [README](quarkus-lambda/README.md) |
| `quarkus-lambda-api-gateway-rest` | Quarkus | Lambda + API Gateway REST | `provided.al2023`, native zip | [README](quarkus-lambda-api-gateway-rest/README.md) |
| `springboot-lambda` | Spring Boot | Lambda + Function URL | `java21`, shaded jar | [README](springboot-lambda/README.md) |
| `springboot-lambda-api-gateway-rest` | Spring Boot | Lambda + API Gateway REST | `java21`, shaded jar | [README](springboot-lambda-api-gateway-rest/README.md) |

## Commands

Everything goes through [Task](https://taskfile.dev) (`task --list`). Each sample is a namespace:
`quarkus`, `quarkus-rest`, `spring`, `spring-rest`.

| Command | Effect |
|---|---|
| `task build` / `test` / `it` | Compile, unit tests, integration tests of every sample |
| `task <ns>:build` / `test` / `it` | Same for one sample |
| `task <ns>:dev` | Local run (Quarkus dev mode or `spring-boot:run`; not available for `quarkus-rest` and `spring`, see their READMEs) |
| `task <ns>:package` | Deployable artifact: native zip (Quarkus, container build) or shaded jar (Spring) |
| `task quarkus:package-jvm` | JVM package, Quarkus only (diagnostic) |
| `task <ns>:tf:init` / `tf:plan` / `tf:apply` / `tf:destroy` | Terraform stack (`apply` and `destroy` are billable or destructive) |
| `task <ns>:invoke` | Call the deployed sample |
| `task lint` / `kics` / `trivy` / `scan` | pre-commit, KICS, Trivy (fail on HIGH/CRITICAL) |

## Tests

| Layer | Name | Plugin | Scope |
|---|---|---|---|
| Unit | `*Test` | surefire (`mvn test`) | Pure logic and in-process framework context: `ProcessingService`, functions through the `FunctionCatalog`, `@QuarkusTest`, `@WebMvcTest` |
| Integration | `*IT` | failsafe (`mvn verify`) | Packaged artifact or real Lambda entry point: `@QuarkusIntegrationTest`; `FunctionInvoker` / `StreamLambdaHandler` fed with a Lambda or API Gateway event |

`-DskipUTs=true` runs the integration tests only, `-DskipITs=true` the unit tests only, `-DskipTests`
neither.

## CI

```text
.github/workflows/
├── _build-java.yml                               # reusable: lint → compile → unit-test → integration-test → package
├── build-quarkus-lambda.yml                      # callers: triggers + concurrency + uses/with only
├── build-quarkus-lambda-api-gateway-rest.yml
├── build-springboot-lambda.yml
├── build-springboot-lambda-api-gateway-rest.yml
├── check-cyber.yml                               # KICS + Trivy config/fs, SARIF upload
└── check-quality.yml                             # pre-commit run --all-files
```

CI holds no cloud credential and never runs `plan`, `apply` or a deployment.
