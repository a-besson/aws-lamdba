package com.springboot.lambda.simpledemo;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives the real Lambda entry point ({@code FunctionInvoker::handleRequest}) end to end,
 * the same handler configured in cloudformation/sam.jvm.yaml and invoked by
 * {@code sam local invoke}.
 */
class FunctionInvokerHandlerTest {

    @BeforeAll
    static void selectDemoFunction() {
        System.setProperty("MAIN_CLASS", Application.class.getName());
        System.setProperty("spring.cloud.function.definition", "demo");
    }

    @Test
    void handleRequestInvokesTheConfiguredDemoFunction() throws Exception {
        FunctionInvoker invoker = new FunctionInvoker();

        var input = new ByteArrayInputStream("{\"body\":\"hello lambda\"}".getBytes(StandardCharsets.UTF_8));
        var output = new ByteArrayOutputStream();

        invoker.handleRequest(input, output, new NoOpContext());

        String response = output.toString(StandardCharsets.UTF_8);
        assertThat(response).contains("\"statusCode\":200").contains("hello lambda");
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
            return "springboot-lambda";
        }

        @Override
        public String getFunctionVersion() {
            return "$LATEST";
        }

        @Override
        public String getInvokedFunctionArn() {
            return "arn:aws:lambda:eu-west-3:000000000000:function:springboot-lambda";
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
            return 256;
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
