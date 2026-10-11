# Quarkus Lambda

Simple Quarkus Lambda using the `quarkus-amazon-lambda` extension, deployed as a native
`provided.al2023` zip behind a Lambda Function URL.

![schema-quarkus-lambda.png](.assets%2Fschema-quarkus-lambda.png)

> :warning: This sample could use some services/options not eligible to free account

## Prerequisites

* Java 21 and Maven (`task setup` at the repository root installs the tooling)
* Docker or Podman, for the Floci integration tests (`task quarkus:it`) and the native build (`task quarkus:package`)
* an AWS account and session, only to deploy

## Getting started

All commands run from the repository root through [Task](https://taskfile.dev).

```shell
task quarkus:dev        # live coding, mock event server on http://localhost:8080
curl -d '{"body":"hello lambda"}' -X POST http://localhost:8080

task quarkus:test       # unit tests (*Test, surefire)
task quarkus:it         # integration tests (*IT, failsafe): packaged JVM artifact, and FlociLambdaIT on the Floci emulator
task quarkus:tf:test      # terraform test: the stack applied on a throwaway Floci container (no AWS account)
task quarkus:package    # native zip target/function.zip (container build)
task quarkus:package-jvm
```

The active handler is selected by `quarkus.lambda.handler` (`src/main/resources/application.properties`,
default `demo`): `demo` echoes the request body, `stream` uppercases the raw payload, `error` always
throws `RuntimeException("Should be unused")`.

`FlociLambdaIT` deploys the JVM `target/function.zip` to the [Floci](https://floci.io) emulator on the `java21`
runtime (Terraform deploys the native zip on `provided.al2023`, which needs a Linux build) and invokes the
`demo`, `stream` and `error` handlers through the Lambda API. See the root README for the container engine setup.

## Deploy to AWS with Terraform

The AWS session is read from the `aws_profile` / `aws_region` Terraform variables
(see `terraform/variables.tf`; pass overrides after `--`).

```bash
task quarkus:package          # build the native zip (`target/function.zip`)
task quarkus:tf:plan          # review what will be created
task quarkus:tf:apply         # WARNING: billable resources
task quarkus:invoke           # call the deployed sample
task quarkus:tf:destroy       # clean up
```

`tf:apply` builds the artifact first (`package` dependency). Example: `task quarkus:tf:plan -- -var aws_profile=my-profile`.

Resources: S3 bucket for the package, Lambda (X-Ray tracing, Lambda Insights layer), Function URL,
CloudWatch log group and a dedicated IAM role, through the `terraform-aws-modules` `s3-bucket` and `lambda` modules.
`task quarkus:tf:init` needs the shared state backend (`backend.hcl`, see the root README). Example call, as run by `task quarkus:invoke`:

```bash
curl -X POST '<lambda-function-url>' \
    -H 'content-type: application/json' \
    -d '{ "body": "hello lambda" }'
```

## Performance test

```bash
npm install -g artillery@latest
artillery run src/test/resources/lambda-load-test.yml --target https://<function-id>.lambda-url.<region>.on.aws/
```

## Structure

```text
quarkus-lambda/
├── Taskfile.yml                 # build, test, it, dev, package, tf:*, invoke
├── src/main/java/.../simpledemo # DemoLambda, StreamLambda, ErrorLambda, ProcessingService, LambdaTracer interceptor
├── src/test/java/.../simpledemo # *Test (QuarkusTest), *IT (QuarkusIntegrationTest) per handler, FlociLambdaIT + FlociEmulator
├── src/test/resources           # payload.json, local.http, lambda-load-test.yml
└── terraform/                   # Lambda, Function URL, IAM, logs
```
