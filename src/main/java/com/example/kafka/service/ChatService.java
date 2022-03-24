package com.example.kafka.service;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService implements ChatServiceInterface{
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Consumer<String, String> consumer;
    private final KafkaAdmin kafkaAdmin;
    private String currentProducer;

    @Autowired
    public ChatService(KafkaTemplate<String, String> kafkaTemplate,
                       Consumer<String, String> consumer,
                       KafkaAdmin kafkaAdmin){
        this.kafkaTemplate = kafkaTemplate;
        this.consumer = consumer;
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public void connect(String producerTopic, String consumerTopic){
        currentProducer = producerTopic;
        consumer.unsubscribe();
        consumer.subscribe(Collections.singleton(consumerTopic));
    }

    @Override
    public List<String> createChat(String chatName) {
        String topicName1 = chatName + "-1";
        String topicName2 = chatName + "-2";
        kafkaAdmin.createOrModifyTopics(new NewTopic(topicName1, 1, (short) 1),
                new NewTopic(topicName2, 1, (short) 1));
        return List.of(topicName1, topicName2);
    }

    @Override
    public void sendMsg(String msg) {
        kafkaTemplate.send(currentProducer, msg);
    }

    @Override
    public List<String> readLastMsg() {
        List<String> result = new ArrayList<>();
        consumer.poll(Duration.ofMillis(100)).forEach((cr) -> result.add(cr.value()));
        return result;
    }
}
