package com.springboot.lambda.simpledemo;

import com.springboot.lambda.simpledemo.aspect.LambdaTracer;
import com.springboot.lambda.simpledemo.common.Request;
import com.springboot.lambda.simpledemo.common.Response;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component("error")
@LambdaTracer
public class ErrorFunction implements Function<Request, Response> {

    @Override
    public Response apply(Request request) {
        throw new RuntimeException("Should be unused");
    }
}
