package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.dtos.ChatDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IChatService {
    public void send(ChatDTO chat, String receiver);

    public List<ChatDTO> receive();

    public CompletableFuture<List<String>> receive(String senderId, String username);
}
