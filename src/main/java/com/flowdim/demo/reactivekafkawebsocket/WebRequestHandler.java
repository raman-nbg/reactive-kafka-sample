package com.flowdim.demo.reactivekafkawebsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class WebRequestHandler {
    @Autowired()
    private KafkaService kafkaService;

    public Mono<ServerResponse> write(ServerRequest request) {
        Mono<String> sendMono = kafkaService.sendMessages(request.bodyToMono(Message.class).map(m -> m.getMessage())).map(l -> "success");
        return ServerResponse.ok().body(sendMono, String.class);
    }
}