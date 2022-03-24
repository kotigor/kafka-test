package com.example.kafka.controller;

import com.example.kafka.service.ChatService;
import com.example.kafka.service.ChatServiceInterface;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {
    private final ChatServiceInterface chatService;

    @Autowired
    public ChatController(ChatServiceInterface chatService){
        this.chatService = chatService;
    }

    @PostMapping(value = "/")
    public List<String> createTopics(@RequestParam String chatName){
        return chatService.createChat(chatName);
    }

    @PostMapping(value = "/connect")
    public String connect(@RequestParam String consumer, @RequestParam String producer){
        chatService.connect(producer, consumer);
        return "success";
    }

    @PostMapping(value = "/send")
    public void sendMsg(@RequestParam String msg){
        chatService.sendMsg(msg);
    }

    @GetMapping(value = "/read")
    public List<String> readMsg(){
        return chatService.readLastMsg();
    }
}
