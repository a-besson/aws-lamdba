# Spring Boot Lambda with API Gateway

Simple Spring Boot MVC Lambda behind API Gateway, functional equivalent of
[`quarkus-lambda-api-gateway-rest`](../quarkus-lambda-api-gateway-rest/README.md), using
`aws-serverless-java-container-springboot3` to run the app unmodified behind API Gateway's proxy
integration. Deployed as a shaded jar on the `java21` runtime.

> :warning: This sample could use some services/options not eligible to free account

## Prerequisites

* Java 21 and Maven (`task setup` at the repository root installs the tooling)
* Docker or Podman, for the Floci integration tests (`task spring-rest:it`)
* an AWS account and session, only to deploy

## Getting started

All commands run from the repository root through [Task](https://taskfile.dev).

Unlike `quarkus-lambda-api-gateway-rest`, this is a plain Spring MVC app, so it can run as a normal
embedded-Tomcat web server for local testing:

```shell
task spring-rest:dev
curl -X GET http://localhost:8080/demo
curl -X POST http://localhost:8080/demo -H 'content-type: application/json' -d '{}'
curl -X PUT http://localhost:8080/demo -H 'content-type: application/json' -d '{}'
```

```shell
task spring-rest:test       # unit tests (*Test, surefire): @WebMvcTest on GET, POST and PUT /demo
task spring-rest:it         # integration tests (*IT, failsafe): API Gateway proxy events through the real handlers, and FlociApiGatewayIT on the Floci emulator
task spring-rest:tf:test      # terraform test: the stack applied on a throwaway Floci container (no AWS account)
task spring-rest:package    # shaded jar target/springboot-lambda-api-gateway-rest.jar
```

`FlociApiGatewayIT` deploys the shaded jar and an API Gateway REST API wired like the Terraform stack (`{proxy+}`,
`ANY`, Lambda proxy integration) to the [Floci](https://floci.io) emulator, then calls `/demo` over HTTP. See the
root README for the container engine setup.

## Deploy to AWS with Terraform

The AWS session is read from the `aws_profile` / `aws_region` Terraform variables
(see `terraform/variables.tf`; pass overrides after `--`).

```bash
task spring-rest:package          # build the shaded jar (`target/springboot-lambda-api-gateway-rest.jar`)
task spring-rest:tf:plan          # review what will be created
task spring-rest:tf:apply         # WARNING: billable resources
task spring-rest:invoke           # call the deployed sample
task spring-rest:tf:destroy       # clean up
```

API Gateway execution logs need the account-wide CloudWatch role: apply `infra/apigateway-account/` once first
(`task apigw-account:tf:apply`, see the root README). `task spring-rest:tf:init` needs the shared state backend
(`backend.hcl`).

`tf:apply` builds the artifact first (`package` dependency). Example: `task spring-rest:tf:plan -- -var aws_profile=my-profile`.

`task spring-rest:invoke` calls `GET`, `POST` and `PUT` on the deployed `/demo` resource:

```bash
user@host % curl -X GET https://<api-id>.execute-api.<region>.amazonaws.com/dev/demo
Hello GET Spring
user@host % curl -X POST https://<api-id>.execute-api.<region>.amazonaws.com/dev/demo
Hello POST Spring
user@host % curl -X PUT https://<api-id>.execute-api.<region>.amazonaws.com/dev/demo
Hello PUT Spring
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
springboot-lambda-api-gateway-rest/
├── Taskfile.yml                 # build, test, it, dev, package, tf:*, invoke
├── src/main/java/.../rest       # Application, RestApiDemo, StreamLambdaHandler (Lambda entry point)
├── src/test/java/.../rest       # RestApiDemoTest (WebMvcTest), StreamLambdaHandlerIT, PayloadJsonHandlerIT, FlociApiGatewayIT + FlociEmulator
├── src/test/resources           # payload.json (API Gateway proxy event), lambda-load-test.yml
└── terraform/                   # Lambda, REST API, stage, IAM, logs, optional custom domain
```
