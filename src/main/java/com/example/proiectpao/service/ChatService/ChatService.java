package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.collection.Chat;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.enums.Penalties;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.repository.ChatRepository;
import com.example.proiectpao.repository.PunishRepository;
import com.example.proiectpao.repository.UserRepository;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ChatService implements IChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PunishRepository punishRepository;

    public ChatService(
            ChatRepository chatRepository,
            UserRepository userRepository,
            PunishRepository punishRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.punishRepository = punishRepository;
    }

    @Override
    @Async
    public void send(ChatDTO chat, String receiver) {
        User sender = userRepository.findByUsernameIgnoreCase(chat.getSenderName());
        User receiverUser = userRepository.findByUsernameIgnoreCase(receiver);
        if (sender == null) throw new NonExistentException("Nu ai cont.");
        if (receiverUser == null) throw new NonExistentException("Userul nu exista.");
        if (!punishRepository
                .findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        sender.getUserId(), Penalties.Mute, new Date())
                .isEmpty())
            throw new UnauthorizedActionException("Esti sanctionat, nu poti trimite mesaje.");
        if (Objects.equals(chat.getSenderName(), receiver))
            throw new UnauthorizedActionException("Nu iti poti da mesaje singur");

        Chat c = new Chat();
        c.setChatId(UUID.randomUUID().toString().split("-")[0]);
        c.setMessage(chat.getMessage());
        c.setSenderName(chat.getSenderName());
        c.setReceiverName(userRepository.findByUsernameIgnoreCase(receiver).getUsername());

        chatRepository.save(c);
    }

    @Override
    @Async
    public CompletableFuture<List<String>> receive(String senderName, String username) {
        System.out.println(senderName + ' ' + username);
        List<String> c = new ArrayList<>();
        List<Chat> chats = chatRepository.findAllBySenderNameAndReceiverName(senderName, username);
        System.out.println(chats.size());
        for (Chat chat : chats) {
            System.out.println(chat.getMessage());
            c.add(chat.getMessage());
        }
        return CompletableFuture.completedFuture(c);
    }
}
