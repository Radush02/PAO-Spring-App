package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.dtos.MessageDTO;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.service.ChatService.IChatService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired private IChatService chatService;

    @PostMapping("/send/{receiver}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> save(@RequestBody ChatDTO chatDTO, @PathVariable String receiver) {
        try {
            CompletableFuture<Boolean> res = chatService.send(chatDTO, receiver);
            return new ResponseEntity<>(res.get(), HttpStatus.CREATED);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/receive")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> receive(@RequestParam String senderName, @RequestParam String username)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<List<MessageDTO>> chatDTOs =
                    chatService.receive(senderName, username);
            return new ResponseEntity<>(chatDTOs.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
