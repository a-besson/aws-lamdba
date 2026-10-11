package com.aws.lambda.quarkus.simpledemo.service;

import com.aws.lambda.quarkus.simpledemo.common.Request;
import com.aws.lambda.quarkus.simpledemo.common.Response;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessingService {

    public Response process(Request input) {
        return new Response(200, input.body());
    }
}
