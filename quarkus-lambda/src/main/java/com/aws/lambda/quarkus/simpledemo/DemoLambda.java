package com.aws.lambda.quarkus.simpledemo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.aws.lambda.quarkus.simpledemo.common.Request;
import com.aws.lambda.quarkus.simpledemo.common.Response;
import com.aws.lambda.quarkus.simpledemo.interceptor.LambdaTracer;
import com.aws.lambda.quarkus.simpledemo.service.ProcessingService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Named("demo")
@LambdaTracer
public class DemoLambda implements RequestHandler<Request, Response> {

    @Inject ProcessingService service;

    @Override
    public Response handleRequest(Request request, Context ctx) {
        return service.process(request);
    }
}
