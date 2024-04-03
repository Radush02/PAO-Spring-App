package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.collection.Chat;
import com.example.proiectpao.dtos.ChatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.proiectpao.repository.ChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService implements IChatService{
    @Autowired
    private ChatRepository chatRepository;

    @Override
    public Chat save(ChatDTO chat) {
        Chat c = new Chat();
        c.setChatId(UUID.randomUUID().toString().split("-")[0]);
        c.setMessage(chat.getMessage());
        c.setSenderId(chat.getSenderId());
        c.setReceiverId(chat.getReceiverId());
        return chatRepository.save(c);
    }
    public List<ChatDTO> receive() {
        List<ChatDTO> c = new ArrayList<>();
        List<Chat> chats = chatRepository.findAll();
        for (Chat chat : chats) {
            ChatDTO chatDTO = new ChatDTO();
            chatDTO.setMessage(chat.getMessage());
            chatDTO.setSenderId(chat.getSenderId());
            chatDTO.setReceiverId(chat.getReceiverId());
            c.add(chatDTO);
        }
        return c;
    }
}
