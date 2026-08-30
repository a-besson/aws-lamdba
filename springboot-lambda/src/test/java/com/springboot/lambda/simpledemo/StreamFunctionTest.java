package com.springboot.lambda.simpledemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.TestPropertySource;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.cloud.function.definition=stream")
class StreamFunctionTest {

    @Autowired
    private FunctionCatalog catalog;

    @Test
    void streamUppercasesRawPayload() {
        Function<Message<String>, String> stream = catalog.lookup(Function.class, "stream");

        Message<String> input = MessageBuilder.withPayload("{\"body\":\"Hello\"}").build();
        String result = stream.apply(input);

        assertThat(result).isEqualTo("{\"BODY\":\"HELLO\"}");
    }
}
