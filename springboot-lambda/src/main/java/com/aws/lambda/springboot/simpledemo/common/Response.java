package com.aws.lambda.springboot.simpledemo.common;

public record Response(Integer statusCode,
                        String body) {
}
