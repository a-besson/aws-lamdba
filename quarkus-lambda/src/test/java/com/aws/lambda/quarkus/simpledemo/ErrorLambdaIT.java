package com.aws.lambda.quarkus.simpledemo;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusIntegrationTest
@TestProfile(ErrorLambdaProfile.class)
public class ErrorLambdaIT extends ErrorLambdaTest {}
