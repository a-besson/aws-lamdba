package com.aws.lambda.springboot.simpledemo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.aws.lambda.springboot.simpledemo.common.Request;
import org.junit.jupiter.api.Test;

class ProcessingServiceTest {

    private final ProcessingService service = new ProcessingService();

    @Test
    void processEchoesBodyWithStatus200() {
        var response = service.process(new Request("Hello"));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Hello");
    }
}
