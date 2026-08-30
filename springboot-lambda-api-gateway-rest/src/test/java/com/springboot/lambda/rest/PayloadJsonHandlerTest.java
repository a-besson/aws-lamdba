package com.springboot.lambda.rest;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives {@code src/test/resources/payload.json} - the same file the Makefile's
 * {@code deploy-local} target and the README's {@code sam local invoke} example use - through
 * the real {@link StreamLambdaHandler} production entry point.
 */
class PayloadJsonHandlerTest {

    @Test
    void handleRequestAcceptsThePayloadJsonFixture() throws Exception {
        StreamLambdaHandler handler = new StreamLambdaHandler();
        var output = new ByteArrayOutputStream();

        try (InputStream payload = getClass().getClassLoader().getResourceAsStream("payload.json")) {
            assertThat(payload).as("src/test/resources/payload.json").isNotNull();
            handler.handleRequest(payload, output, new NoOpContext());
        }

        String response = output.toString(StandardCharsets.UTF_8);
        assertThat(response).contains("\"statusCode\":200").contains("Hello GET Spring");
    }

    private static final class NoOpContext implements Context {
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
}
