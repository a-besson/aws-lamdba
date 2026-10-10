package com.aws.lambda.springboot.simpledemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Drives the real Lambda entry point ({@code FunctionInvoker::handleRequest}), the handler
 * configured in terraform/lambda.tf, with the same event as {@code src/test/resources/payload.json}.
 * The active function is selected like on AWS, through {@code spring.cloud.function.definition}.
 */
class FunctionInvokerHandlerIT {

    @BeforeAll
    static void selectMainClass() {
        System.setProperty("MAIN_CLASS", Application.class.getName());
    }

    @AfterEach
    void resetFunctionDefinition() {
        System.clearProperty("spring.cloud.function.definition");
    }

    @Test
    void handleRequestInvokesTheConfiguredDemoFunction() throws Exception {
        String response = invoke("demo", payloadJson());

        assertThat(response).contains("\"statusCode\":200").contains("hello lambda");
    }

    @Test
    void handleRequestPropagatesTheFailureOfTheErrorFunction() throws Exception {
        String event = payloadJson();

        assertThatThrownBy(() -> invoke("error", event))
                .isInstanceOf(RuntimeException.class)
                .hasStackTraceContaining("Should be unused");
    }

    private static String payloadJson() throws IOException {
        try (InputStream payload = FunctionInvokerHandlerIT.class.getResourceAsStream("/payload.json")) {
            assertThat(payload).as("src/test/resources/payload.json").isNotNull();
            return new String(payload.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String invoke(String functionDefinition, String event) throws Exception {
        System.setProperty("spring.cloud.function.definition", functionDefinition);
        var input = new ByteArrayInputStream(event.getBytes(StandardCharsets.UTF_8));
        var output = new ByteArrayOutputStream();

        new FunctionInvoker().handleRequest(input, output, new NoOpContext());

        return output.toString(StandardCharsets.UTF_8);
    }
}
