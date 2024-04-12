package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.collection.Chat;
import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.repository.ChatRepository;
import com.example.proiectpao.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ChatService implements IChatService {
    @Autowired private ChatRepository chatRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Async
    public void send(ChatDTO chat, String receiver) {
        Chat c = new Chat();
        c.setChatId(UUID.randomUUID().toString().split("-")[0]);
        c.setMessage(chat.getMessage());
        c.setSenderId(chat.getSenderId());
        c.setReceiverId(userRepository.findByUsernameIgnoreCase(receiver).getUserId());
        if (Objects.equals(c.getSenderId(), c.getReceiverId())) return;
        chatRepository.save(c);
    }

    @Override
    public List<ChatDTO> receive() {
        return new ArrayList<>();
    }

    //    public List<ChatDTO> receive() {
    //        List<ChatDTO> c = new ArrayList<>();
    //        List<Chat> chats = chatRepository.findAll();
    //        for (Chat chat : chats) {
    //            ChatDTO chatDTO = new ChatDTO();
    //            chatDTO.setMessage(chat.getMessage());
    //            chatDTO.setSenderId(chat.getSenderId());
    //            chatDTO.setReceiverId(chat.getReceiverId());
    //            c.add(chatDTO);
    //        }
    //        return c;
    //    }
    @Override
    @Async
    public CompletableFuture<List<String>> receive(String senderId, String username) {
        List<String> c = new ArrayList<>();
        List<Chat> chats =
                chatRepository.findAllBySenderIdAndReceiverId(
                        senderId, userRepository.findByUsernameIgnoreCase(username).getUserId());
        for (Chat chat : chats) c.add(chat.getMessage());
        return CompletableFuture.completedFuture(c);
    }
}
