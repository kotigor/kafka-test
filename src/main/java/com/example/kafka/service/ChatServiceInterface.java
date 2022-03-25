package com.example.kafka.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatServiceInterface {
    List<List<String>> createChat(String chatName) ;
    void sendMsg(String msg);
    List<String> readLastMsg();
    void connect(String topic, int producerPartition, int consumerPartition);
}
