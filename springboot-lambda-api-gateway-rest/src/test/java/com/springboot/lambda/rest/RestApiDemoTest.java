package com.springboot.lambda.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestApiDemo.class)
class RestApiDemoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDemoReturnsHelloGetSpring() throws Exception {
        mockMvc.perform(get("/demo"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello GET Spring"));
    }

    @Test
    void postDemoReturnsHelloPostSpring() throws Exception {
        mockMvc.perform(post("/demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"hello lambda\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello POST Spring"));
    }

    @Test
    void putDemoReturnsHelloPutSpring() throws Exception {
        mockMvc.perform(put("/demo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"hello lambda\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello PUT Spring"));
    }
}
