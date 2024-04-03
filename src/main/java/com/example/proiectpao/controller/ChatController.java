package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.service.ChatService.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private IChatService chatService;
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody ChatDTO chatDTO) {
        chatService.save(chatDTO);
    }
    @GetMapping("/receive")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ChatDTO>> receive() {
        System.out.println("ChatController.receive");
        List<ChatDTO> chatDTOs = chatService.receive();
        return new ResponseEntity<>(chatDTOs, HttpStatus.OK);
    }
}
