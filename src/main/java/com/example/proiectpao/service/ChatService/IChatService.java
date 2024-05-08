package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.dtos.MessageDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

public interface IChatService {
    @Async
    CompletableFuture<Boolean> send(ChatDTO chat, String receiver);

    @Async
    CompletableFuture<List<MessageDTO>> receive(String senderId, String username);
}
