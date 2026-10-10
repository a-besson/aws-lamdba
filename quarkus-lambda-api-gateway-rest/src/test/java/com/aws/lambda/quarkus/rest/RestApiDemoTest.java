package com.aws.lambda.quarkus.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
class RestApiDemoTest {

    @Test
    public void testGetDemo() {
        given()
            .accept("application/json")
            .when()
            .get("/demo")
            .then()
            .statusCode(200)
            .body(equalTo("Hello GET RESTEasy"));
    }

    @Test
    public void testPostDemo() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body("{\"body\":\"hello lambda\"}")
            .when()
            .post("/demo")
            .then()
            .statusCode(200)
            .body(equalTo("Hello POST RESTEasy"));
    }

    @Test
    public void testPutDemo() {
        given()
            .contentType("application/json")
            .accept("application/json")
            .body("{\"body\":\"hello lambda\"}")
            .when()
            .put("/demo")
            .then()
            .statusCode(200)
            .body(equalTo("Hello PUT RESTEasy"));
    }
}
