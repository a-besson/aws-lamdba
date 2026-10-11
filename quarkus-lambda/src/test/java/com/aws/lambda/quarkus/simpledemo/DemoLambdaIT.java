package com.aws.lambda.quarkus.simpledemo;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusIntegrationTest
@TestProfile(DemoLambdaProfile.class)
public class DemoLambdaIT extends DemoLambdaTest {}
