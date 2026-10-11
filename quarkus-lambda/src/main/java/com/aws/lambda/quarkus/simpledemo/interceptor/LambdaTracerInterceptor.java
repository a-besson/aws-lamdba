package com.aws.lambda.quarkus.simpledemo.interceptor;

import com.amazonaws.services.lambda.runtime.Context;
import com.aws.lambda.quarkus.simpledemo.common.Request;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@LambdaTracer
@Priority(1)
@Interceptor
@Slf4j
public class LambdaTracerInterceptor {

    @AroundInvoke
    Object logInvocation(InvocationContext context) throws Exception {
        var request =
                Arrays.stream(context.getParameters())
                        .filter(p -> p.getClass().isAssignableFrom(Request.class))
                        .findFirst()
                        .orElse(Request.empty());

        Context ctx =
                (Context)
                        Arrays.stream(context.getParameters())
                                .filter(p -> p instanceof Context)
                                .findFirst()
                                .orElseThrow();

        log.info(
                "Function name: {}, Function version: {}, Request id: {} Identity: {}, Client ctx: {} Request:{}",
                ctx.getFunctionName(),
                ctx.getFunctionVersion(),
                ctx.getAwsRequestId(),
                ctx.getIdentity(),
                ctx.getClientContext(),
                request);

        Object ret = context.proceed();
        return ret;
    }
}
