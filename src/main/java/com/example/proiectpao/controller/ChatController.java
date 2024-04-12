package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.service.ChatService.IChatService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired private IChatService chatService;

    @PostMapping("/send/{receiver}")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody ChatDTO chatDTO, @PathVariable String receiver) {
        chatService.send(chatDTO, receiver);
    }

    @GetMapping("/receive")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ChatDTO>> receive() {
        System.out.println("ChatController.receive");
        List<ChatDTO> chatDTOs = chatService.receive();
        return new ResponseEntity<>(chatDTOs, HttpStatus.OK);
    }

    @GetMapping("/receive/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> receive(
            @RequestBody String senderId, @PathVariable String username)
            throws ExecutionException, InterruptedException {
        CompletableFuture<List<String>> chatDTOs = chatService.receive(senderId, username);
        return new ResponseEntity<>(chatDTOs.get(), HttpStatus.OK);
    }
}
