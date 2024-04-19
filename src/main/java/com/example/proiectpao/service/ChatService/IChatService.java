package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.dtos.ChatDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

public interface IChatService {
    @Async
    void send(ChatDTO chat, String receiver);

    @Async
    CompletableFuture<List<ChatDTO>> receive();

    @Async
    CompletableFuture<List<String>> receive(String senderId, String username);
}
