package com.aws.lambda.springboot.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.ApiGatewayRequestIdentity;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.Headers;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Drives an {@link AwsProxyRequest} through {@link SpringBootLambdaContainerHandler} - the same
 * container handler {@link StreamLambdaHandler} initializes - proving the API Gateway proxy
 * integration wiring works without deploying anything.
 */
class StreamLambdaHandlerIT {

    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    @BeforeAll
    static void setUp() throws ContainerInitializationException {
        handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(Application.class);
    }

    @Test
    void getDemoThroughApiGatewayProxyIntegration() {
        AwsProxyResponse response = handler.proxy(requestFor("GET"), new NoOpContext());

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello GET Spring");
    }

    @Test
    void postDemoThroughApiGatewayProxyIntegration() {
        AwsProxyResponse response = handler.proxy(requestFor("POST"), new NoOpContext());

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello POST Spring");
    }

    @Test
    void putDemoThroughApiGatewayProxyIntegration() {
        AwsProxyResponse response = handler.proxy(requestFor("PUT"), new NoOpContext());

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello PUT Spring");
    }

    private static AwsProxyRequest requestFor(String httpMethod) {
        AwsProxyRequest request = new AwsProxyRequest();
        request.setHttpMethod(httpMethod);
        request.setPath("/demo");
        request.setBody("{\"body\":\"hello lambda\"}");

        Headers headers = new Headers();
        headers.putSingle("Content-Type", "application/json");
        headers.putSingle("Accept", "application/json");
        request.setMultiValueHeaders(headers);

        ApiGatewayRequestIdentity identity = new ApiGatewayRequestIdentity();
        identity.setSourceIp("127.0.0.1");

        AwsProxyRequestContext requestContext = new AwsProxyRequestContext();
        requestContext.setRequestId("test-request-id");
        requestContext.setStage("dev");
        requestContext.setResourcePath("/demo");
        requestContext.setHttpMethod(httpMethod);
        requestContext.setIdentity(identity);
        request.setRequestContext(requestContext);

        return request;
    }
}
