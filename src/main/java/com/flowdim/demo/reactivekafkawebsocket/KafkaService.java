package com.flowdim.demo.reactivekafkawebsocket;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface KafkaService {
    Mono<Long> sendMessages(Flux<Message> message);
}
