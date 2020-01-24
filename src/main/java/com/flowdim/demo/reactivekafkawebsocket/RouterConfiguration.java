package com.flowdim.demo.reactivekafkawebsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class RouterConfiguration {
    @Autowired
    WebRequestHandler handler;

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions
                .route(PUT("/messages").and(contentType(APPLICATION_STREAM_JSON).or(contentType(APPLICATION_JSON))), handler::write);
    }
}
