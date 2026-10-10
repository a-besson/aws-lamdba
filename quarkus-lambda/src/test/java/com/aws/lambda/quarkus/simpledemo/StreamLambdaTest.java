package com.aws.lambda.quarkus.simpledemo;

import com.aws.lambda.quarkus.simpledemo.common.Request;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
@TestProfile(StreamLambdaProfile.class)
public class StreamLambdaTest {

    @Test
    public void testSimpleLambdaSuccess() throws Exception {
        // you test your lambdas by invoking on http://localhost:8081
        // this works in dev mode too

        Request in = new Request("Hello");
        given()
            .contentType("application/json")
            .accept("application/json")
            .body(in)
            .when().post()
            .then()
            .statusCode(200)
            .body(containsString("{\"BODY\":\"HELLO\"}"));
    }

}
