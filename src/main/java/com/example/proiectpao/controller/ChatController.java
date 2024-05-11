package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.ChatDTO;
import com.example.proiectpao.dtos.ImportMessageDTO;
import com.example.proiectpao.dtos.MessageDTO;
import com.example.proiectpao.dtos.MessageExportDTO;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.service.ChatService.IChatService;
import com.example.proiectpao.utils.Pair;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Clasa controller pentru chat.
 * Nu este implementat folosind Websocket.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired private IChatService chatService;

    /**
     * API POST pentru a trimite un mesaj.
     * @param chatDTO (DTO-ul ce contine mesajul si userul ce trimite mesajul)
     * @param receiver numele utilizatorului ce primeste mesajul
     * @return true sau mesajul erorii.
     */
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

    /**
     * API POST pentru a exporta mesajele dintre 2 useri.<br>
     * Pentru ca nu putem trimite atat numele fisierului cat si fisierul efectiv, am retinut numele fisierului in header-ul Content-Disposition.
     * @param messageDTO (DTO-ul ce contine userul care vrea sa exporteze mesajele si cei doi useri ce vorbesc)
     * @return Fisierul efectiv sau mesajul erorii.
     */
    @PostMapping("/export")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin(
            origins = "http://localhost:4200",
            allowedHeaders = "*",
            exposedHeaders = "Content-Disposition")
    public ResponseEntity<?> export(@RequestBody MessageExportDTO messageDTO) {
        try {
            CompletableFuture<Pair<Resource, String>> res = chatService.exportChat(messageDTO);
            HttpHeaders headers = new HttpHeaders();
            ContentDisposition contentDisposition =
                    ContentDisposition.builder("attachment")
                            .filename(res.get().getSecond())
                            .build();
            headers.setContentDisposition(contentDisposition);
            return ResponseEntity.ok().headers(headers).body(res.get().getFirst());
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a primi mesaje.
     * @param senderName numele celui cu care user-ul vorbeste
     * @param username numele user-ului
     * @return O lista de DTO-uri ce contine mesajele userilor si data la care au fost trimise acestea sau mesajul erorii.
     */
    @GetMapping("/receive")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> receive(
            @RequestParam String senderName, @RequestParam String username) {
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

    /**
     * API POST pentru a incarca un fisier cu mesaje.
     * @param file fisierul .json cu mesaje
     * @param requester numele utilizatorului ce incarca fisierul
     * @param sender numele utilizatorului ce a trimis mesajele
     * @param receiver numele utilizatorului ce a primit mesajele
     * @return true sau mesajul erorii.
     */
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("requester") String requester,
            @RequestParam("sender") String sender,
            @RequestParam("receiver") String receiver) {
        try {
            ImportMessageDTO messageDTO = new ImportMessageDTO();
            messageDTO.setRequester(requester);
            messageDTO.setSender(sender);
            messageDTO.setReceiver(receiver);
            messageDTO.setFile(file);
            CompletableFuture<Boolean> res = chatService.importChat(messageDTO);
            return new ResponseEntity<>(res.get(), HttpStatus.CREATED);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
