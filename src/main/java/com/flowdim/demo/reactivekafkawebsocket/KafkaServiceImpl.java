package com.flowdim.demo.reactivekafkawebsocket;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.text.SimpleDateFormat;
import java.util.Properties;

@Service
public class KafkaServiceImpl implements KafkaService {
    private final SimpleDateFormat dateFormat;
    private KafkaSender<String, Message> sender;

    KafkaServiceImpl() {
        createProducer();
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }

    public Mono<Long> sendMessages(Flux<Message> messages) {
        Flux<SenderRecord<String, Message, Message>> records = messages
                .map(m -> new ProducerRecord<String, Message>("/RAMAN/ingest.stream:unchecked", null, m))
                .map(producerRecord -> SenderRecord.create(producerRecord, producerRecord.value()));

        return sender.send(records)
            .doOnEach((Signal<SenderResult<Message>> senderResult) -> {
                if (senderResult.hasError()) {
                    System.out.println(senderResult.get().exception().toString());
                }
            })
            .filter(senderResult -> senderResult.exception() != null)
            .map(senderResult -> senderResult.recordMetadata().offset())
            .next();
    }

    private void createProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        producerProperties.put(ProducerConfig.ACKS_CONFIG, "all");

        SenderOptions<String, Message> senderOptions = SenderOptions.create(producerProperties);

        sender = KafkaSender.create(senderOptions);
    }
}
