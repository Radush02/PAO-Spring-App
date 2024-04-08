package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.dtos.ChatDTO;
import java.util.List;

public interface IChatService {
    public void send(ChatDTO chat, String receiver);

    public List<ChatDTO> receive();

    public List<String> receive(String senderId, String username);
}
