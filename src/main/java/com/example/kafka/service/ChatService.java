package com.example.kafka.service;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
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
    private int currentProducerPartition;

    @Autowired
    public ChatService(KafkaTemplate<String, String> kafkaTemplate,
                       Consumer<String, String> consumer,
                       KafkaAdmin kafkaAdmin){
        this.kafkaTemplate = kafkaTemplate;
        this.consumer = consumer;
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public void connect(String topic, int producerPartition, int consumerPartition){
        currentProducer = topic;
        currentProducerPartition = producerPartition;
        consumer.unsubscribe();
        consumer.assign(Collections.singleton(new TopicPartition(topic, consumerPartition)));
    }

    @Override
    public List<List<String>> createChat(String chatName) {
        String topicName1 = chatName + "-1";
        kafkaAdmin.createOrModifyTopics(new NewTopic(topicName1, 2, (short) 1));
        return List.of(List.of(topicName1, "0"), List.of(topicName1, "1"));
    }

    @Override
    public void sendMsg(String msg) {
        kafkaTemplate.send(currentProducer, currentProducerPartition,"", msg);
    }

    @Override
    public List<String> readLastMsg() {
        List<String> result = new ArrayList<>();
        consumer.poll(Duration.ofMillis(100)).forEach((cr) -> result.add(cr.value()));
        return result;
    }
}
