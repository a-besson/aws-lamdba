# AWS Lambda — Quarkus & Spring Boot samples

Sibling AWS Lambda samples built with Quarkus and with Spring Boot, showing functionally
equivalent implementations of the same two Lambda shapes (plain Lambda behind a Function URL, and
Lambda behind API Gateway REST) on each stack. Each sample ships its Terraform stack.

## Prerequisites

```bash
task setup    # Brewfile (Maven, Temurin 21, Terraform, TFLint, Trivy, Podman...), git hooks, TFLint plugins
task doctor   # check the tools and Java 21; an AWS session is only needed to deploy
```

`task it` needs a container engine (Podman, as in the Brewfile, or Docker): the integration tests start the
[Floci](https://floci.io) AWS emulator and the Lambda runtime images.

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
| `task build` / `test` / `it` | Compile (with Checkstyle and the Spotless check), unit tests, integration tests of every sample |
| `task format` | Format the Java sources (Spotless, google-java-format AOSP) |
| `task <ns>:build` / `test` / `it` | Same for one sample |
| `task <ns>:dev` | Local run (Quarkus dev mode or `spring-boot:run`; not available for `quarkus-rest` and `spring`, see their READMEs) |
| `task <ns>:package` | Deployable artifact: native zip (Quarkus, container build) or shaded jar (Spring) |
| `task quarkus:package-jvm` | JVM package, Quarkus only (diagnostic) |
| `task <ns>:tf:init` / `tf:plan` / `tf:apply` / `tf:destroy` | Terraform stack (`apply` and `destroy` are billable or destructive); `tf:init` needs [`backend.hcl`](#terraform-state-and-shared-stacks) |
| `task <ns>:tf:test` | `terraform test` of the stack on a throwaway Floci container (fake credentials, no AWS account, no package needed) |
| `task apigw-account:tf:*`, `task state:tf:*` | One-off shared stacks: API Gateway account settings, Terraform state bucket |
| `task <ns>:invoke` | Call the deployed sample |
| `task lint` / `kics` / `trivy` / `scan` | pre-commit, KICS, Trivy (fail on HIGH/CRITICAL) |

## Tests

| Layer | Name | Plugin | Scope |
|---|---|---|---|
| Unit | `*Test` | surefire (`mvn test`) | Pure logic and in-process framework context: `ProcessingService`, functions through the `FunctionCatalog`, `@QuarkusTest`, `@WebMvcTest` |
| Integration | `*IT` | failsafe (`mvn verify`) | Packaged artifact or real Lambda entry point: `@QuarkusIntegrationTest`; `FunctionInvoker` / `StreamLambdaHandler` fed with a Lambda or API Gateway event |
| Integration (emulator) | `Floci*IT` | failsafe | The packaged artifact deployed to the Floci AWS emulator and called through the Lambda API (Function URL samples) or an API Gateway REST API over HTTP (REST samples) |

`-DskipUTs=true` runs the integration tests only, `-DskipITs=true` the unit tests only, `-DskipTests`
neither.

### Floci integration tests

Each sample has a `Floci*IT` class that starts the emulator with Testcontainers, deploys the packaged artifact
with the configuration of its Terraform stack (runtime, handler, environment, `{proxy+}` resource with a Lambda
proxy integration) and invokes it. Lambda functions run in the real `public.ecr.aws/lambda/java:21` image, started by
Floci through the container engine socket. Nothing is sent to AWS and no credential is involved.

* The image is pinned by the `floci.image` property of the root `pom.xml` (Renovate keeps it up to date).
* The Quarkus samples deploy the JVM `target/function.zip` on `java21`: the native zip Terraform deploys on
  `provided.al2023` needs a Linux build, which is too slow for `task it`.
* Each module keeps its own `FlociEmulator` helper (no shared test module), a few lines of Testcontainers and AWS
  SDK client setup.
* `task <ns>:it` (and `task it`) configure Podman: the Testcontainers client talks to the Podman machine socket and
  Ryuk is disabled, as it cannot run there. A `DOCKER_HOST` already set is used as is, and so is a plain Docker
  (CI). The Lambda containers are removed with the functions at the end of each class.
* The first run pulls `floci/floci` and the Lambda runtime image (about 0.5 GB). The Podman machine needs about 2 GiB.

## Terraform state and shared stacks

* `backend "s3"` with `use_lockfile = true` in every stack: the state lives in a versioned, encrypted S3 bucket and is
  locked with a lockfile, without DynamoDB. The bucket name never reaches the repository: copy
  `backend.hcl.example` to the git-ignored `backend.hcl`; `task <ns>:tf:init` adds the per-stack key.
  CI validates with `-backend=false`.
* `infra/state-bucket/` creates that bucket (local state, one-off): set `state_bucket` in a git-ignored
  `terraform.tfvars`, run `task state:tf:apply`, and put its `backend_config` output in `backend.hcl`.
  To move an existing local `terraform.tfstate`, run `task <ns>:tf:migrate-state`.
* `infra/apigateway-account/` holds `aws_api_gateway_account`, a singleton per account and region that the two REST stacks
  used to declare each (the second apply overwrote the first). Apply it once, before the REST stacks
  (`task apigw-account:tf:apply`). A REST stack already applied with the previous version still has the role, its
  policy and the account setting in its state: import them into `infra/apigateway-account` or `terraform state rm` them
  before the next apply, otherwise the role is destroyed.
* The Lambda function and the artifact bucket use the official `terraform-aws-modules/lambda` and `s3-bucket` modules
  (exact versions); `moved` blocks keep the existing state addresses. API Gateway REST stays on native resources:
  `terraform-aws-modules/apigateway-v2` only covers HTTP and WebSocket APIs.

## CI

```text
.github/workflows/
├── _build-java.yml                               # reusable: lint → (terraform-test ∥ compile → unit-test → integration-test) → package
├── build-quarkus-lambda.yml                      # callers: triggers + concurrency + uses/with only
├── build-quarkus-lambda-api-gateway-rest.yml
├── build-springboot-lambda.yml
├── build-springboot-lambda-api-gateway-rest.yml
├── check-cyber.yml                               # KICS + Trivy config/fs, SARIF upload
└── check-quality.yml                             # pre-commit run --all-files + CodeQL (Java, PR and weekly)
```

CI holds no cloud credential and never runs `plan`, `apply` or a deployment.
