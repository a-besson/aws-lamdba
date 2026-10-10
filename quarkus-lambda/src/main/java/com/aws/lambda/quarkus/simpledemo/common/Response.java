package com.aws.lambda.quarkus.simpledemo.common;

public record Response(Integer statusCode,
                       String body) {
}
