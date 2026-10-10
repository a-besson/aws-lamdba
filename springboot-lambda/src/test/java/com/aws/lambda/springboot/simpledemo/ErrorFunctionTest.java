package com.aws.lambda.springboot.simpledemo;

import com.aws.lambda.springboot.simpledemo.common.Request;
import com.aws.lambda.springboot.simpledemo.common.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.test.context.TestPropertySource;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = "spring.cloud.function.definition=error")
class ErrorFunctionTest {

    @Autowired
    private FunctionCatalog catalog;

    @Test
    void errorFunctionAlwaysFails() {
        Function<Request, Response> error = catalog.lookup(Function.class, "error");

        assertThatThrownBy(() -> error.apply(new Request("Hello")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Should be unused");
    }
}
