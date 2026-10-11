package com.aws.lambda.springboot.simpledemo.common;

public record Request(String body) {

    public static Request empty() {
        return new Request("");
    }
}
