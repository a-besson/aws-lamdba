package com.springboot.lambda.rest;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.ApiGatewayRequestIdentity;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.Headers;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives an {@link AwsProxyRequest} through {@link SpringBootLambdaContainerHandler} - the same
 * container handler {@link StreamLambdaHandler} initializes - proving the API Gateway proxy
 * integration wiring works without deploying anything.
 */
class StreamLambdaHandlerTest {

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
