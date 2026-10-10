package com.aws.lambda.quarkus.rest;

import io.quarkus.amazon.lambda.http.model.AwsProxyRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
class RestApiDemoTest {

    @Test
    public void testRestApiDemo() throws Exception {
        RestAssured.defaultParser = Parser.JSON;
        var request = new AwsProxyRequest();
        request.setBody("hello lambda");

        given()
            .contentType("application/json")
            .accept("application/json")
            .body(request)
            .when()
            .get("/demo")
            .then()
            .statusCode(200)
            .body(containsString("Hello"));
    }
}
