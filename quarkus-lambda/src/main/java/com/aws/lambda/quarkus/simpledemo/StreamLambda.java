package com.aws.lambda.quarkus.simpledemo;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.aws.lambda.quarkus.simpledemo.interceptor.LambdaTracer;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Named("stream")
@LambdaTracer
public class StreamLambda implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context ctx) throws IOException {
        log.info("""
                Function name: {}, Function version: {}, Request id: {}
                Identity: {}, Client ctx: {}
                Request:{}""", ctx.getFunctionName(),
            ctx.getFunctionVersion(),
            ctx.getAwsRequestId(),
            ctx.getIdentity(),
            ctx.getClientContext(),
            inputStream.toString());

        int letter;
        while ((letter = inputStream.read()) != -1) {
            int character = Character.toUpperCase(letter);
            outputStream.write(character);
        }
    }
}
