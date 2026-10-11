package com.aws.lambda.springboot.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/**
 * Drives {@code src/test/resources/payload.json}, an API Gateway proxy event, through the real
 * {@link StreamLambdaHandler} production entry point.
 */
class PayloadJsonHandlerIT {

    @Test
    void handleRequestAcceptsThePayloadJsonFixture() throws Exception {
        StreamLambdaHandler handler = new StreamLambdaHandler();
        var output = new ByteArrayOutputStream();

        try (InputStream payload =
                getClass().getClassLoader().getResourceAsStream("payload.json")) {
            assertThat(payload).as("src/test/resources/payload.json").isNotNull();
            handler.handleRequest(payload, output, new NoOpContext());
        }

        String response = output.toString(StandardCharsets.UTF_8);
        assertThat(response).contains("\"statusCode\":200").contains("Hello GET Spring");
    }
}
