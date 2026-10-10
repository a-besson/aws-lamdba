package com.aws.lambda.springboot.simpledemo;

import com.aws.lambda.springboot.simpledemo.aspect.LambdaTracer;
import com.aws.lambda.springboot.simpledemo.common.Request;
import com.aws.lambda.springboot.simpledemo.common.Response;
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
