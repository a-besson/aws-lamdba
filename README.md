# AWS Lambda Quarkus & Spring Boot

Sibling AWS Lambda samples built with Quarkus and with Spring Boot, showing functionally
equivalent implementations of the same two Lambda shapes (plain Lambda, and Lambda behind API
Gateway) on each stack.

### Getting Started

```bash
# build every module, Quarkus native included (Spring modules build in plain JVM mode)
mvn install -DskipTests -Dnative -Dquarkus.native.container-build=true
```

### Templates

##### Quarkus

###### [Quarkus Lambda Sample](quarkus-lambda/README.md)

###### [Quarkus Lambda with API Gateway Sample](quarkus-lambda-api-gateway-rest/README.md)

##### Spring Boot

###### [Spring Boot Lambda Sample](springboot-lambda/README.md)

###### [Spring Boot Lambda with API Gateway Sample](springboot-lambda-api-gateway-rest/README.md)
