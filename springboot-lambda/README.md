# Spring Boot Lambda

Simple Spring Cloud Function lambda, functional equivalent of [`quarkus-lambda`](../quarkus-lambda/README.md),
using `spring-cloud-function-adapter-aws`.

> :warning: This sample could use some services/options not eligible to free account

## Prerequisites

* java 21
* aws account
* aws cli & sam cli

## Getting started

### Run locally

#### Testing the functions

```shell
mvn test
```

This runs the JUnit tests that invoke `demo` and `stream` through the Spring Cloud Function
`FunctionCatalog`, and `FunctionInvoker` end to end - the same handler entry point used on AWS
Lambda and by `sam local invoke` below.

#### Package

```shell
# build the shaded jar deployed to AWS Lambda (java21 runtime)
mvn clean install -DskipTests
```

#### Testing locally with the SAM

```shell
sam local invoke --template cloudformation/sam.jvm.yaml --event ./src/test/resources/payload.json --region eu-west-3
```

#### Deploy to AWS with SAM & Cloudformation

```shell
# create bucket for lambda
aws s3 mb s3://${LAMDBA_BUCKET};

# build app
mvn clean install -DskipTests

# package app with sam & upload lambda
sam package --template-file cloudformation/sam.jvm.yaml \
    --output-template-file target/packaged.yaml \
    --s3-bucket ${LAMDBA_BUCKET};

# Deploy lambda stack
sam deploy --template-file target/packaged.yaml \
  --stack-name ${LAMDBA_STACK} \
  --capabilities CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND

# Test lambda
curl -X POST 'https://ID.lambda-url.eu-west-3.on.aws/' \
  -H 'content-type: application/json' \
  -d '{ "body": "hello lambda" }'

{"statusCode":200,"body":"hello lambda"}
```

#### Deploy to AWS with SAM & Terraform

```bash
cd terraform/

terraform init
terraform plan
terraform apply -auto-approve
...
Apply complete! Resources: 9 added, 0 changed, 0 destroyed.
Outputs:
aws_lambda_function = "arn:aws:lambda:ZONE:ID:function:cloud-public-springboot-lambda"
aws_lambda_function_url = "https://ID.lambda-url.eu-west-3.on.aws/"

curl -X POST 'https://ID.lambda-url.eu-west-3.on.aws/' \
    -H 'content-type: application/json' \
    -d '{ "body": "hello lambda" }'

{"statusCode":200,"body":"hello lambda"}

terraform destroy -auto-approve
```

#### Performance test

```bash
npm install -g artillery@latest
artillery run src/test/resources/lambda-load-test.yml --target https://example.lambda-url.eu-west-3.on.aws/
```

### Selecting a different function

Like the Quarkus sample's `quarkus.lambda.handler`, this module selects the active function via
`spring.cloud.function.definition` (`src/main/resources/application.properties`, default `demo`),
overridden in the SAM/Terraform templates through the `SPRING_CLOUD_FUNCTION_DEFINITION`
environment variable. Set it to `stream` to uppercase the raw request payload, or `error` to
always throw `RuntimeException("Should be unused")`.
