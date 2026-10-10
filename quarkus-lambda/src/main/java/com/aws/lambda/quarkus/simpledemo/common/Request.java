package com.aws.lambda.quarkus.simpledemo.common;

public record Request(String body) {

    public static Request empty() {
        return new Request("");
    }
}
