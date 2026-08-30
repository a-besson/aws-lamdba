package com.springboot.lambda.simpledemo;

import com.springboot.lambda.simpledemo.common.Request;
import com.springboot.lambda.simpledemo.common.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.test.context.TestPropertySource;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.cloud.function.definition=demo")
class DemoFunctionTest {

    @Autowired
    private FunctionCatalog catalog;

    @Test
    void demoEchoesRequestBodyWithStatus200() {
        Function<Request, Response> demo = catalog.lookup(Function.class, "demo");

        Response response = demo.apply(new Request("Hello"));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Hello");
    }
}
