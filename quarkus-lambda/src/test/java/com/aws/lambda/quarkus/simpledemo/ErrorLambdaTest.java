package com.aws.lambda.quarkus.simpledemo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import com.aws.lambda.quarkus.simpledemo.common.Request;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(ErrorLambdaProfile.class)
public class ErrorLambdaTest {

    @Test
    public void testErrorLambdaReportsTheFailure() {
        given().contentType("application/json")
                .accept("application/json")
                .body(new Request("Hello"))
                .when()
                .post()
                .then()
                .statusCode(500)
                .body("errorType", equalTo("java.lang.RuntimeException"))
                .body("errorMessage", equalTo("Should be unused"));
    }
}
