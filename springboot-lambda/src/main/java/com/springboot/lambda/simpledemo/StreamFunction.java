package com.springboot.lambda.simpledemo;

import com.springboot.lambda.simpledemo.aspect.LambdaTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Functional equivalent of the Quarkus {@code StreamLambda}. Spring Cloud Function has no
 * direct analogue of {@code RequestStreamHandler}, so this uppercases the raw request payload
 * instead of an {@link java.io.InputStream} - same observable behavior, idiomatic AWS adapter shape.
 */
@Slf4j
@Component("stream")
@LambdaTracer
public class StreamFunction implements Function<Message<String>, String> {

    @Override
    public String apply(Message<String> message) {
        return message.getPayload().toUpperCase();
    }
}
