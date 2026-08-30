package com.springboot.lambda.simpledemo.common;

public record Response(Integer statusCode,
                        String body) {
}
