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
@CrossOrigin(origins = "http://localhost:4200")
public class LobbyController {

    @Autowired private final ILobbyService lobbyService;

    public LobbyController(ILobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping("/getLeaders")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getLeaders() throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<?> l = lobbyService.getLobbyLeaders();
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @GetMapping("/inLobby/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> inLobby(@PathVariable String username)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<String> l = lobbyService.inLobby(username);
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/inLobby/{lobby}/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> inLobby(@PathVariable String lobby, @PathVariable String username)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<Boolean> l = lobbyService.inLobby(lobby, username);
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/getLobbies")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getLobbies() throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<?> l = lobbyService.getLobbies();
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
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
