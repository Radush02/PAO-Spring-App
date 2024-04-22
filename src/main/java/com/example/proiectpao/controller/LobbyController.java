package com.example.proiectpao.controller;

import com.example.proiectpao.collection.Lobby;
import com.example.proiectpao.dtos.lobbyDTOs.CreateLobbyDTO;
import com.example.proiectpao.dtos.lobbyDTOs.JoinLobbyDTO;
import com.example.proiectpao.dtos.lobbyDTOs.KickLobbyDTO;
import com.example.proiectpao.exceptions.AlreadyExistsException;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.service.LobbyService.ILobbyService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

    @Autowired private final ILobbyService lobbyService;

    public LobbyController(ILobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createLobby(@RequestBody CreateLobbyDTO lobbyDTO)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<Lobby> l = lobbyService.createLobby(lobbyDTO);
            return new ResponseEntity<>(l.get(), HttpStatus.CREATED);
        } catch (AlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> joinLobby(@RequestBody JoinLobbyDTO lobbyDTO)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<Lobby> l = lobbyService.joinLobby(lobbyDTO);
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (AlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/kick")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> kickFromLobby(@RequestBody KickLobbyDTO lobbyDTO)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<Lobby> l = lobbyService.kickFromLobby(lobbyDTO);
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
