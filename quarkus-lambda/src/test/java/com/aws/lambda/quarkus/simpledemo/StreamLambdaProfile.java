package com.aws.lambda.quarkus.simpledemo;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Collections;
import java.util.Map;

public class StreamLambdaProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Collections.singletonMap("quarkus.lambda.handler", "stream");
    }

    @Override
    public String getConfigProfile() {
        return "test-lambda-profile";
    }
}
