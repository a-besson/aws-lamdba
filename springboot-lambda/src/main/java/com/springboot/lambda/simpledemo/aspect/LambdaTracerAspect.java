package com.springboot.lambda.simpledemo.aspect;

import com.amazonaws.services.lambda.runtime.Context;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Spring AOP equivalent of the Quarkus {@code LambdaTracerInterceptor}: logs the AWS
 * {@link Context} around invocations of {@link LambdaTracer}-annotated functions.
 * <p>
 * Spring Cloud Function only exposes the AWS {@code Context} as the {@code aws-context}
 * header of a {@link Message}, and only for functions typed to accept a {@code Message}.
 * Plain POJO-typed functions (e.g. {@code Function<Request, Response>}) never see the
 * header, so this aspect degrades gracefully and logs without context details whenever
 * no {@code Message} argument is present - which is also what happens in plain unit tests
 * that call a function bean directly.
 */
@Aspect
@Component
@Slf4j
public class LambdaTracerAspect {

    static final String AWS_CONTEXT_HEADER = "aws-context";

    @Around("@within(com.springboot.lambda.simpledemo.aspect.LambdaTracer)")
    public Object logInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        Context ctx = Arrays.stream(joinPoint.getArgs())
                .filter(Message.class::isInstance)
                .map(Message.class::cast)
                .map(message -> message.getHeaders().get(AWS_CONTEXT_HEADER, Context.class))
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (ctx != null) {
            log.info("Function name: {}, Function version: {}, Request id: {} Identity: {}, Client ctx: {} Request:{}",
                    ctx.getFunctionName(), ctx.getFunctionVersion(), ctx.getAwsRequestId(), ctx.getIdentity(),
                    ctx.getClientContext(), Arrays.toString(joinPoint.getArgs()));
        } else {
            log.info("No AWS context available for this invocation, Request:{}", Arrays.toString(joinPoint.getArgs()));
        }

        return joinPoint.proceed();
    }
}
