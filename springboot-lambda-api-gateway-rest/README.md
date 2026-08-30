# Spring Boot Lambda with API Gateway

Simple Spring Boot MVC lambda behind API Gateway, functional equivalent of
[`quarkus-lambda-api-gateway-rest`](../quarkus-lambda-api-gateway-rest/README.md), using
`aws-serverless-java-container-springboot3` to run the app unmodified behind API Gateway's proxy
integration.

> :warning: This sample could use some services/options not eligible to free account

## Prerequisites

* java 21
* aws account
* aws cli & sam cli

## Getting started

### Run locally

#### Live coding and testing

Unlike `quarkus-lambda-api-gateway-rest`, this is a plain Spring MVC app, so it can run as a
normal embedded-Tomcat web server for local testing:

```shell
mvn spring-boot:run
curl -X GET http://localhost:8080/demo
curl -X POST http://localhost:8080/demo -H 'content-type: application/json' -d '{}'
curl -X PUT http://localhost:8080/demo -H 'content-type: application/json' -d '{}'
```

#### Package

```shell
# build the shaded jar deployed to AWS Lambda (java21 runtime)
mvn install -DskipTests
```

#### Testing locally with the SAM

```shell
sam local invoke --template cloudformation/sam.jvm.yaml --event ./src/test/resources/payload.json --region eu-west-3
```

### Deploy & test

```bash
# build & deploy to aws
make all
....
-------------------------------------------------------------------------------------------------
Outputs
-------------------------------------------------------------------------------------------------
....
Key                 BasicAWSApiGateway
Description         API Gateway endpoint URL for Staging stage for Hello World function
Value               https://ID.execute-api.eu-west-3.amazonaws.com/dev/demo/
-------------------------------------------------------------------------------------------------

user@mbp % curl -X GET https://ID.execute-api.eu-west-3.amazonaws.com/dev/demo/
Hello GET Spring
user@mbp % curl -X POST https://ID.execute-api.eu-west-3.amazonaws.com/dev/demo/
Hello POST Spring
user@mbp % curl -X PUT https://ID.execute-api.eu-west-3.amazonaws.com/dev/demo/
Hello PUT Spring
```

#### Deploy to AWS with SAM & Cloudformation

```shell
# create bucket for lambda
aws s3 mb s3://${LAMDBA_BUCKET};

# build app
mvn install -DskipTests

# package app with sam & upload lambda
sam package --template-file cloudformation/sam.jvm.yaml \
    --output-template-file target/packaged.yaml \
    --s3-bucket ${LAMDBA_BUCKET};

# Deploy lambda stack
sam deploy --template-file target/packaged.yaml \
  --stack-name ${LAMDBA_STACK} \
  --capabilities CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND
```

#### Deploy to AWS with SAM & Terraform

```bash
cd terraform/

terraform init
terraform plan
terraform apply -auto-approve
...
Apply complete! Resources: 12 added, 0 changed, 0 destroyed.
Outputs:
aws_lambda_function = "arn:aws:lambda:ZONE:ID:function:cloud-public-springboot-lambda"

curl -X GET https://ID.execute-api.eu-west-3.amazonaws.com/dev/demo
curl -X POST https://ID.execute-api.eu-west-3.amazonaws.com/dev/demo
curl -X PUT https://ID.execute-api.eu-west-3.amazonaws.com/dev/demo

terraform destroy -auto-approve
```

The optional `api-domain-name.tf` wires the API Gateway stage to a custom domain
(`var.domain_name`) via ACM + Route53 - it is not required to exercise the sample and can be
removed if you do not own a Route53-hosted zone.

#### Performance test

```bash
npm install -g artillery@latest
artillery run src/test/resources/lambda-load-test.yml --target https://ID.execute-api.eu-west-3.amazonaws.com
```
