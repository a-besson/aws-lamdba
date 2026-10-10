# Spring Boot Lambda

Simple Spring Cloud Function Lambda, functional equivalent of [`quarkus-lambda`](../quarkus-lambda/README.md),
using `spring-cloud-function-adapter-aws`, deployed as a shaded jar on the `java21` runtime behind a
Lambda Function URL.

> :warning: This sample could use some services/options not eligible to free account

## Prerequisites

* Java 21 and Maven (`task setup` at the repository root installs the tooling)
* an AWS account and session, only to deploy

## Getting started

All commands run from the repository root through [Task](https://taskfile.dev).

```shell
task spring:test       # unit tests (*Test, surefire): functions through the FunctionCatalog
task spring:it         # integration tests (*IT, failsafe): FunctionInvoker fed with src/test/resources/payload.json
task spring:package    # shaded jar target/springboot-lambda.jar
```

This module is a Spring Cloud Function without web server, so there is no `dev` task: its local
execution goes through `task spring:it`, which drives the real `FunctionInvoker` entry point.

### Selecting a different function

Like the Quarkus sample's `quarkus.lambda.handler`, this module selects the active function via
`spring.cloud.function.definition` (`src/main/resources/application.properties`, default `demo`),
overridden in the Terraform stack through the `SPRING_CLOUD_FUNCTION_DEFINITION` environment variable.
Set it to `stream` to uppercase the raw request payload, or `error` to always throw
`RuntimeException("Should be unused")`.

## Deploy to AWS with Terraform

The AWS session is read from the `aws_profile` / `aws_region` Terraform variables
(see `terraform/variables.tf`; pass overrides after `--`).

```bash
task spring:package          # build the shaded jar (`target/springboot-lambda.jar`)
task spring:tf:plan          # review what will be created
task spring:tf:apply         # WARNING: billable resources
task spring:invoke           # call the deployed sample
task spring:tf:destroy       # clean up
```

`tf:apply` builds the artifact first (`package` dependency). Example: `task spring:tf:plan -- -var aws_profile=my-profile`.

Example call, as run by `task spring:invoke`:

```bash
curl -X POST '<lambda-function-url>' \
    -H 'content-type: application/json' \
    -d '{ "body": "hello lambda" }'

{"statusCode":200,"body":"hello lambda"}
```

## Performance test

```bash
npm install -g artillery@latest
artillery run src/test/resources/lambda-load-test.yml --target https://<function-id>.lambda-url.<region>.on.aws/
```

## Structure

```text
springboot-lambda/
├── Taskfile.yml                 # build, test, it, package, tf:*, invoke
├── src/main/java/.../simpledemo # DemoFunction, StreamFunction, ErrorFunction, ProcessingService, LambdaTracer aspect
├── src/test/java/.../simpledemo # *Test (FunctionCatalog) and FunctionInvokerHandlerIT
├── src/test/resources           # payload.json, lambda-load-test.yml
└── terraform/                   # Lambda, Function URL, IAM, logs
```
