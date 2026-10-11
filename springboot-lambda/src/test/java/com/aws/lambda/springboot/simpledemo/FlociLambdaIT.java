package com.aws.lambda.springboot.simpledemo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.Environment;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.Runtime;

/**
 * Deploys the packaged jar to the Floci emulator with the configuration of terraform/lambda.tf
 * (runtime, handler, environment) and invokes it through the Lambda API, in the real {@code java21}
 * runtime image.
 */
class FlociLambdaIT {

    private static final String HANDLER =
            "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest";
    private static final String ROLE = "arn:aws:iam::000000000000:role/springboot-lambda-execution";

    private static FlociEmulator floci;
    private static LambdaClient lambda;

    @BeforeAll
    static void startEmulatorAndDeploy() throws IOException {
        floci = new FlociEmulator().start();
        lambda = floci.lambda();
        deploy("demo-function", "demo");
        deploy("error-function", "error");
    }

    @AfterAll
    static void stopEmulator() {
        if (lambda != null) {
            // removes the function containers, which outlive the emulator otherwise
            lambda.listFunctions()
                    .functions()
                    .forEach(f -> lambda.deleteFunction(r -> r.functionName(f.functionName())));
            lambda.close();
        }
        if (floci != null) {
            floci.close();
        }
    }

    @Test
    void invokeReturnsTheDemoResponse() throws IOException {
        InvokeResponse response = invoke("demo-function");

        assertThat(response.functionError()).isNull();
        assertThat(response.payload().asUtf8String())
                .contains("\"statusCode\":200")
                .contains("hello lambda");
    }

    @Test
    void invokeReportsTheFailureOfTheErrorFunction() throws IOException {
        InvokeResponse response = invoke("error-function");

        assertThat(response.functionError()).isNotNull();
        assertThat(response.payload().asUtf8String()).contains("Should be unused");
    }

    private static void deploy(String name, String functionDefinition) throws IOException {
        byte[] jar = Files.readAllBytes(Path.of("target", "springboot-lambda.jar"));
        lambda.createFunction(
                r ->
                        r.functionName(name)
                                .runtime(Runtime.JAVA21)
                                .handler(HANDLER)
                                .role(ROLE)
                                .memorySize(256)
                                .timeout(60)
                                .environment(
                                        Environment.builder()
                                                .variables(
                                                        Map.of(
                                                                "SPRING_CLOUD_FUNCTION_DEFINITION",
                                                                functionDefinition))
                                                .build())
                                .code(
                                        FunctionCode.builder()
                                                .zipFile(SdkBytes.fromByteArray(jar))
                                                .build()));
    }

    private static InvokeResponse invoke(String name) throws IOException {
        byte[] event;
        try (InputStream payload = FlociLambdaIT.class.getResourceAsStream("/payload.json")) {
            assertThat(payload).as("src/test/resources/payload.json").isNotNull();
            event = payload.readAllBytes();
        }
        return lambda.invoke(r -> r.functionName(name).payload(SdkBytes.fromByteArray(event)));
    }
}
