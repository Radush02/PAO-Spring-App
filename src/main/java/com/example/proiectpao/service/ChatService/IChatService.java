package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.collection.Chat;
import com.example.proiectpao.dtos.ChatDTO;

import java.util.List;

public interface IChatService {
    public Chat save(ChatDTO chat);
    public List<ChatDTO> receive();
}
