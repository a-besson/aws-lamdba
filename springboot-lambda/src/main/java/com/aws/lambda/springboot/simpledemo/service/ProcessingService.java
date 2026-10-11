package com.aws.lambda.springboot.simpledemo.service;

import com.aws.lambda.springboot.simpledemo.common.Request;
import com.aws.lambda.springboot.simpledemo.common.Response;
import org.springframework.stereotype.Component;

@Component
public class ProcessingService {

    public Response process(Request input) {
        return new Response(200, input.body());
    }
}
