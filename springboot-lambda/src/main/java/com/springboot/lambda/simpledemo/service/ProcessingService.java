package com.springboot.lambda.simpledemo.service;

import com.springboot.lambda.simpledemo.common.Request;
import com.springboot.lambda.simpledemo.common.Response;
import org.springframework.stereotype.Component;

@Component
public class ProcessingService {

    public Response process(Request input) {
        return new Response(200, input.body());
    }
}
