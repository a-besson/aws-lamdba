package com.aws.lambda.springboot.simpledemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.aws.lambda.springboot.simpledemo.common.Request;
import com.aws.lambda.springboot.simpledemo.common.Response;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.cloud.function.definition=demo")
class DemoFunctionTest {

    @Autowired private FunctionCatalog catalog;

    @Test
    void demoEchoesRequestBodyWithStatus200() {
        Function<Request, Response> demo = catalog.lookup(Function.class, "demo");

        Response response = demo.apply(new Request("Hello"));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Hello");
    }
}
