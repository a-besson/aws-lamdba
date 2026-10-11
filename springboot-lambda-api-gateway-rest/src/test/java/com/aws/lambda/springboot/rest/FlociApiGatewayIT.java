package com.aws.lambda.springboot.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.IntegrationType;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.Runtime;

/**
 * Deploys the packaged jar and an API Gateway REST API wired like terraform/ (Lambda proxy
 * integration on {@code {proxy+}}) to the Floci emulator, then calls the API over HTTP. The Lambda
 * runs in the real {@code java21} runtime image.
 */
class FlociApiGatewayIT {

    private static final String FUNCTION = "springboot-rest-function";
    private static final String HANDLER =
            "com.aws.lambda.springboot.rest.StreamLambdaHandler::handleRequest";
    private static final String ROLE =
            "arn:aws:iam::000000000000:role/springboot-lambda-rest-execution";
    private static final String EVENT = "{\"body\":\"hello lambda\"}";

    private static FlociEmulator floci;
    private static LambdaClient lambda;
    private static ApiGatewayClient apiGateway;
    private static String apiUrl;

    @BeforeAll
    static void startEmulatorAndDeploy() throws IOException {
        floci = new FlociEmulator().start();
        lambda = floci.lambda();
        apiGateway = floci.apiGateway();
        String functionArn = deployFunction();
        apiUrl = deployRestApi(functionArn);
    }

    @AfterAll
    static void stopEmulator() {
        if (apiGateway != null) {
            apiGateway.close();
        }
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
    void getDemoThroughApiGateway() throws Exception {
        HttpResponse<String> response = call("GET", "/demo");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Hello GET Spring");
    }

    @Test
    void postDemoThroughApiGateway() throws Exception {
        HttpResponse<String> response = call("POST", "/demo");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Hello POST Spring");
    }

    @Test
    void putDemoThroughApiGateway() throws Exception {
        HttpResponse<String> response = call("PUT", "/demo");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Hello PUT Spring");
    }

    @Test
    void unknownPathIsRejectedByTheApplication() throws Exception {
        HttpResponse<String> response = call("GET", "/unknown");

        assertThat(response.statusCode()).isEqualTo(404);
    }

    private static String deployFunction() throws IOException {
        byte[] jar =
                Files.readAllBytes(Path.of("target", "springboot-lambda-api-gateway-rest.jar"));
        return lambda.createFunction(
                        r ->
                                r.functionName(FUNCTION)
                                        .runtime(Runtime.JAVA21)
                                        .handler(HANDLER)
                                        .role(ROLE)
                                        .memorySize(512)
                                        .timeout(60)
                                        .code(
                                                FunctionCode.builder()
                                                        .zipFile(SdkBytes.fromByteArray(jar))
                                                        .build()))
                .functionArn();
    }

    /**
     * Same API as terraform/api-gateway.tf: REGIONAL, one {proxy+} resource, ANY method, Lambda
     * proxy integration.
     */
    private static String deployRestApi(String functionArn) {
        String apiId =
                apiGateway
                        .createRestApi(r -> r.name("main").binaryMediaTypes("application/json"))
                        .id();
        String rootId = apiGateway.getResources(r -> r.restApiId(apiId)).items().get(0).id();
        String proxyId =
                apiGateway
                        .createResource(
                                r -> r.restApiId(apiId).parentId(rootId).pathPart("{proxy+}"))
                        .id();
        apiGateway.putMethod(
                r ->
                        r.restApiId(apiId)
                                .resourceId(proxyId)
                                .httpMethod("ANY")
                                .authorizationType("NONE"));
        apiGateway.putIntegration(
                r ->
                        r.restApiId(apiId)
                                .resourceId(proxyId)
                                .httpMethod("ANY")
                                .type(IntegrationType.AWS_PROXY)
                                .integrationHttpMethod("POST")
                                .uri(
                                        "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/"
                                                + functionArn
                                                + "/invocations"));
        lambda.addPermission(
                r ->
                        r.functionName(FUNCTION)
                                .statementId("apigateway-invoke")
                                .action("lambda:InvokeFunction")
                                .principal("apigateway.amazonaws.com"));
        apiGateway.createDeployment(r -> r.restApiId(apiId).stageName("dev"));
        return floci.endpoint() + "/restapis/" + apiId + "/dev/_user_request_";
    }

    private static HttpResponse<String> call(String method, String path)
            throws IOException, InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder(URI.create(apiUrl + path))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofString(EVENT))
                        .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
