package com.aws.lambda.springboot.rest;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * Minimal {@link Context} for tests that call a Lambda entry point directly.
 */
final class NoOpContext implements Context {

    @Override
    public String getAwsRequestId() {
        return "test-request-id";
    }

    @Override
    public String getLogGroupName() {
        return "test-log-group";
    }

    @Override
    public String getLogStreamName() {
        return "test-log-stream";
    }

    @Override
    public String getFunctionName() {
        return "springboot-lambda-api-gateway-rest";
    }

    @Override
    public String getFunctionVersion() {
        return "$LATEST";
    }

    @Override
    public String getInvokedFunctionArn() {
        return "arn:aws:lambda:eu-west-3:000000000000:function:springboot-lambda-api-gateway-rest";
    }

    @Override
    public CognitoIdentity getIdentity() {
        return null;
    }

    @Override
    public ClientContext getClientContext() {
        return null;
    }

    @Override
    public int getRemainingTimeInMillis() {
        return 30000;
    }

    @Override
    public int getMemoryLimitInMB() {
        return 512;
    }

    @Override
    public LambdaLogger getLogger() {
        return new LambdaLogger() {
            @Override
            public void log(String message) {
            }

            @Override
            public void log(byte[] message) {
            }
        };
    }
}
