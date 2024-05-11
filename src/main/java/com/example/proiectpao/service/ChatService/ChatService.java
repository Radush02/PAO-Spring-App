package com.example.proiectpao.service.ChatService;

import com.example.proiectpao.collection.Chat;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.dtos.ImportMessageDTO;
import com.example.proiectpao.dtos.MessageDTO;
import com.example.proiectpao.dtos.MessageExportDTO;
import com.example.proiectpao.enums.Penalties;
import com.example.proiectpao.enums.Role;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.repository.ChatRepository;
import com.example.proiectpao.repository.PunishRepository;
import com.example.proiectpao.repository.UserRepository;
import com.example.proiectpao.service.S3Service.S3Service;
import com.example.proiectpao.utils.FileParser.FileParser;
import com.example.proiectpao.utils.FileParser.JsonFileParser;
import com.example.proiectpao.utils.Pair;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ChatService implements IChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final PunishRepository punishRepository;
    private final JsonFileParser jsonFileParser;
    private final S3Service s3Service;

    public ChatService(
            ChatRepository chatRepository,
            UserRepository userRepository,
            PunishRepository punishRepository,
            S3Service s3Service) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.punishRepository = punishRepository;
        this.jsonFileParser = FileParser.getInstance(JsonFileParser.class);
        this.s3Service = s3Service;
    }

    @Override
    @Async
    public CompletableFuture<Boolean> importChat(ImportMessageDTO dto) throws IOException {
        User requester = userRepository.findByUsernameIgnoreCase(dto.getRequester());
        User receiver = userRepository.findByUsernameIgnoreCase(dto.getReceiver());
        User sender = userRepository.findByUsernameIgnoreCase(dto.getSender());
        if (requester == null) throw new NonExistentException("Nu ai cont.");
        if (sender == null || receiver == null) throw new NonExistentException("Userul nu exista.");
        if (requester.getRole() != Role.Admin
                && (!Objects.equals(requester.getUsername(), receiver.getUsername())
                        && !Objects.equals(requester.getUsername(), sender.getUsername()))) {
            System.out.println(requester.getRole());
            System.out.println(!Objects.equals(requester.getUsername(), receiver.getUsername()));
            System.out.println(!Objects.equals(requester.getUsername(), sender.getUsername()));
            throw new UnauthorizedActionException("Nu ai permisiuni de a face asta.");
        }
        jsonFileParser.read(null, dto.getFile(), s3Service, chatRepository);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async
    public CompletableFuture<Pair<Resource, String>> exportChat(MessageExportDTO dto)
            throws IOException {
        User requester = userRepository.findByUsernameIgnoreCase(dto.getRequester());
        User receiver = userRepository.findByUsernameIgnoreCase(dto.getReceiver());
        User sender = userRepository.findByUsernameIgnoreCase(dto.getSender());
        if (requester == null) throw new NonExistentException("Nu ai cont.");
        if (sender == null || receiver == null) throw new NonExistentException("Userul nu exista.");
        if (requester.getRole() != Role.Admin
                && (!Objects.equals(requester.getUsername(), receiver.getUsername())
                        && !Objects.equals(requester.getUsername(), sender.getUsername()))) {
            System.out.println(requester.getRole());
            System.out.println(!Objects.equals(requester.getUsername(), receiver.getUsername()));
            System.out.println(!Objects.equals(requester.getUsername(), sender.getUsername()));
            throw new UnauthorizedActionException("Nu ai permisiuni de a face asta.");
        }
        List<Chat> chats =
                chatRepository.findAllBySenderNameAndReceiverName(
                        dto.getSender(), dto.getReceiver());
        chats.addAll(
                chatRepository.findAllBySenderNameAndReceiverName(
                        dto.getReceiver(), dto.getSender()));
        chats.sort(Comparator.comparing(Chat::getDate));
        String json = new Gson().toJson(chats);
        String nume = jsonFileParser.write(json, s3Service);
        return CompletableFuture.completedFuture(
                new Pair<>(
                        new InputStreamResource(
                                s3Service.getFile(nume + ".json").getObjectContent()),
                        nume + ".json"));
    }

    @Override
    @Async
    public CompletableFuture<Boolean> send(ChatDTO chat, String receiver) {
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
        c.setDate(new Date());
        chatRepository.save(c);
        return CompletableFuture.completedFuture(true);
    }

    @Override
    @Async
    public CompletableFuture<List<MessageDTO>> receive(String senderName, String username) {
        List<MessageDTO> c = new ArrayList<>();
        List<Chat> chats = chatRepository.findAllBySenderNameAndReceiverName(senderName, username);
        chats.addAll(chatRepository.findAllBySenderNameAndReceiverName(username, senderName));
        chats.sort(Comparator.comparing(Chat::getDate));
        for (Chat chat : chats) {
            c.add(new MessageDTO(chat.getMessage(), chat.getSenderName(), chat.getDate()));
        }
        return CompletableFuture.completedFuture(c);
    }
}
