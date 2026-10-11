package com.aws.lambda.quarkus.simpledemo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
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
 * Deploys the JVM {@code target/function.zip} to the Floci emulator and invokes it through the
 * Lambda API, in the real {@code java21} runtime image. Terraform deploys the native zip on {@code
 * provided.al2023}: the native binary needs a Linux build, so the JVM package of the same function
 * stands in for it here. The function is selected like on AWS, through {@code
 * QUARKUS_LAMBDA_HANDLER}.
 */
public class FlociLambdaIT {

    private static final String HANDLER =
            "io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest";
    private static final String ROLE = "arn:aws:iam::000000000000:role/quarkus-lambda-execution";
    private static final String EVENT = "{\"body\":\"hello lambda\"}";

    private static FlociEmulator floci;
    private static LambdaClient lambda;

    @BeforeAll
    public static void startEmulatorAndDeploy() throws IOException {
        floci = new FlociEmulator().start();
        lambda = floci.lambda();
        deploy("demo-function", "demo");
        deploy("stream-function", "stream");
        deploy("error-function", "error");
    }

    @AfterAll
    public static void stopEmulator() {
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
    public void invokeReturnsTheDemoResponse() {
        InvokeResponse response = invoke("demo-function");

        assertThat(response.functionError(), nullValue());
        assertThat(response.payload().asUtf8String(), containsString("hello lambda"));
    }

    @Test
    public void invokeUppercasesTheEventWithTheStreamFunction() {
        InvokeResponse response = invoke("stream-function");

        assertThat(response.functionError(), nullValue());
        assertThat(
                response.payload().asUtf8String(), containsString("{\"BODY\":\"HELLO LAMBDA\"}"));
    }

    @Test
    public void invokeReportsTheFailureOfTheErrorFunction() {
        InvokeResponse response = invoke("error-function");

        assertThat(response.functionError(), notNullValue());
        assertThat(response.payload().asUtf8String(), containsString("Should be unused"));
    }

    private static void deploy(String name, String handlerName) throws IOException {
        byte[] zip = Files.readAllBytes(Path.of("target", "function.zip"));
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
                                                                "QUARKUS_LAMBDA_HANDLER",
                                                                handlerName))
                                                .build())
                                .code(
                                        FunctionCode.builder()
                                                .zipFile(SdkBytes.fromByteArray(zip))
                                                .build()));
    }

    private static InvokeResponse invoke(String name) {
        return lambda.invoke(r -> r.functionName(name).payload(SdkBytes.fromUtf8String(EVENT)));
    }
}
