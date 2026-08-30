package com.springboot.lambda.rest;

import com.amazonaws.serverless.proxy.RequestReader;
import com.amazonaws.services.lambda.runtime.Context;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("demo")
public class RestApiDemo {

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public String findDemo(HttpServletRequest request) {
        logInvocation(request);
        return "Hello GET Spring";
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public String demo(HttpServletRequest request) {
        logInvocation(request);
        return "Hello POST Spring";
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public String updateDemo(HttpServletRequest request) {
        logInvocation(request);
        return "Hello PUT Spring";
    }

    private void logInvocation(HttpServletRequest request) {
        Context ctx = (Context) request.getAttribute(RequestReader.LAMBDA_CONTEXT_PROPERTY);
        if (ctx != null) {
            log.info("Function name: {}, Function version: {}, Request id: {} Identity: {}, Client ctx: {}",
                    ctx.getFunctionName(), ctx.getFunctionVersion(), ctx.getAwsRequestId(), ctx.getIdentity(),
                    ctx.getClientContext());
        } else {
            log.info("No AWS Lambda context available (running outside Lambda)");
        }
    }
}
