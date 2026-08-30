package com.springboot.lambda.simpledemo;

import com.springboot.lambda.simpledemo.aspect.LambdaTracer;
import com.springboot.lambda.simpledemo.common.Request;
import com.springboot.lambda.simpledemo.common.Response;
import com.springboot.lambda.simpledemo.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component("demo")
@LambdaTracer
@RequiredArgsConstructor
public class DemoFunction implements Function<Request, Response> {

    private final ProcessingService service;

    @Override
    public Response apply(Request request) {
        return service.process(request);
    }
}
