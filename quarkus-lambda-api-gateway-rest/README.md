# Quarkus Lambda with API Gateway

Simple Quarkus Lambda using the `quarkus-amazon-lambda-rest` extension (RESTEasy), deployed as a
native `provided.al2023` zip behind an API Gateway REST API (`AWS_PROXY` integration).

> :warning: This sample could use some services/options not eligible to free account

## Prerequisites

* Java 21 and Maven (`task setup` at the repository root installs the tooling)
* Docker or Podman, for the Floci integration tests (`task quarkus-rest:it`) and the native build (`task quarkus-rest:package`)
* an AWS account and session, only to deploy

## Getting started

All commands run from the repository root through [Task](https://taskfile.dev).

```shell
task quarkus-rest:test       # unit tests (*Test, surefire): GET, POST and PUT /demo
task quarkus-rest:it         # integration tests (*IT, failsafe): packaged JVM artifact, and FlociApiGatewayIT on the Floci emulator
task quarkus-rest:tf:test      # terraform test: the stack applied on a throwaway Floci container (no AWS account)
task quarkus-rest:package    # native zip target/function.zip (container build)
task quarkus-rest:package-jvm
```

There is no `dev` task: `quarkus:dev` fails on `quarkus-amazon-lambda-rest` 3.39.1 (`Key already
registered quarkus.http.local-base-uri`). Run the sample locally through `*Test` / `*IT`.

`FlociApiGatewayIT` deploys the JVM `target/function.zip` and an API Gateway REST API wired like the Terraform stack
(`{proxy+}`, `ANY`, Lambda proxy integration) to the [Floci](https://floci.io) emulator, then calls `/demo` over HTTP.
See the root README for the container engine setup.

## Deploy to AWS with Terraform

The AWS session is read from the `aws_profile` / `aws_region` Terraform variables
(see `terraform/variables.tf`; pass overrides after `--`).

```bash
task quarkus-rest:package          # build the native zip (`target/function.zip`)
task quarkus-rest:tf:plan          # review what will be created
task quarkus-rest:tf:apply         # WARNING: billable resources
task quarkus-rest:invoke           # call the deployed sample
task quarkus-rest:tf:destroy       # clean up
```

API Gateway execution logs need the account-wide CloudWatch role: apply `infra/apigateway-account/` once first
(`task apigw-account:tf:apply`, see the root README). `task quarkus-rest:tf:init` needs the shared state backend
(`backend.hcl`).

`tf:apply` builds the artifact first (`package` dependency). Example: `task quarkus-rest:tf:plan -- -var aws_profile=my-profile`.

`task quarkus-rest:invoke` calls `GET`, `POST` and `PUT` on the deployed `/demo` resource:

```bash
user@host % curl -X GET https://<api-id>.execute-api.<region>.amazonaws.com/dev/demo
Hello GET RESTEasy
user@host % curl -X POST https://<api-id>.execute-api.<region>.amazonaws.com/dev/demo
Hello POST RESTEasy
user@host % curl -X PUT https://<api-id>.execute-api.<region>.amazonaws.com/dev/demo
Hello PUT RESTEasy
```

The optional `api-domain-name.tf` wires the API Gateway stage to a custom domain (`var.domain_name`)
through ACM and Route53: it requires a Route53-hosted zone you own.

## Performance test

```bash
npm install -g artillery@latest
artillery run src/test/resources/lambda-load-test.yml --target https://<api-id>.execute-api.<region>.amazonaws.com
```

## Structure

```text
quarkus-lambda-api-gateway-rest/
├── Taskfile.yml                 # build, test, it, package, tf:*, invoke
├── src/main/java/.../rest       # RestApiDemo (GET, POST, PUT /demo)
├── src/test/java/.../rest       # RestApiDemoTest (QuarkusTest), RestApiDemoIT (QuarkusIntegrationTest), FlociApiGatewayIT + FlociEmulator
└── terraform/                   # Lambda, REST API, stage, IAM, logs, optional custom domain
```
