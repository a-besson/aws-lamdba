package com.aws.lambda.quarkus.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import lombok.extern.slf4j.Slf4j;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
@Path("demo")
public class RestApiDemo {

    @GET
    @Produces(APPLICATION_JSON)
    public String findDemo(@Context com.amazonaws.services.lambda.runtime.Context ctx) {
        log.info("Function name: {}, Function version: {}, Request id: {} Identity: {}, Client ctx: {}", ctx.getFunctionName(),
            ctx.getFunctionVersion(),
            ctx.getAwsRequestId(),
            ctx.getIdentity(),
            ctx.getClientContext());
        return "Hello GET RESTEasy";
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public String demo(@Context com.amazonaws.services.lambda.runtime.Context ctx) {
        log.info("Function name: {}, Function version: {}, Request id: {} Identity: {}, Client ctx: {}", ctx.getFunctionName(),
            ctx.getFunctionVersion(),
            ctx.getAwsRequestId(),
            ctx.getIdentity(),
            ctx.getClientContext());
        return "Hello POST RESTEasy";
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public String updateDemo(@Context com.amazonaws.services.lambda.runtime.Context ctx) {
        log.info("Function name: {}, Function version: {}, Request id: {} Identity: {}, Client ctx: {}", ctx.getFunctionName(),
            ctx.getFunctionVersion(),
            ctx.getAwsRequestId(),
            ctx.getIdentity(),
            ctx.getClientContext());
        return "Hello PUT RESTEasy";
    }
}
