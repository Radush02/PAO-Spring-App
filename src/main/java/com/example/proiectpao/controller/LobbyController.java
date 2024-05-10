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

    /**
     * API GET pentru a prelua liderii tuturor lobby-urilor.
     * @return Lista cu liderii lobby-urilor sau mesajul erorii.
     */
    @GetMapping("/getLeaders")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getLeaders() {
        try {
            CompletableFuture<?> l = lobbyService.getLobbyLeaders();
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API Post pentru a crea un lobby.
     * @param lobbyDTO (DTO-ul ce contine numele lobby-ului si numele liderului)
     * @return Lobby-ul creat sau mesajul erorii.
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createLobby(@RequestBody CreateLobbyDTO lobbyDTO) {
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

    /**
     * API GET pentru a verifica daca un utilizator este in lobby.
     * @param username numele utilizatorului
     * @return true sau mesajul erorii.
     */
    @GetMapping("/inLobby/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> inLobby(@PathVariable String username)
             {
        try {
            CompletableFuture<String> l = lobbyService.inLobby(username);
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a verifica daca un utilizator este intr-un anumit lobby.
     * @param lobby numele lobby-ului
     * @param username numele utilizatorului
     * @return true sau mesajul erorii.
     */
    @GetMapping("/inLobby/{lobby}/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> inLobby(@PathVariable String lobby, @PathVariable String username){
        try {
            CompletableFuture<Boolean> l = lobbyService.inLobby(lobby, username);
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a prelua lobby-urile.
     * @return Lista cu lobby-urile sau mesajul erorii.
     */
    @GetMapping("/getLobbies")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getLobbies(){
        try {
            CompletableFuture<?> l = lobbyService.getLobbies();
            return new ResponseEntity<>(l.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a intra intr-un lobby.
     * @param lobbyDTO (DTO-ul ce contine numele lobby-ului, numele utilizatorului si daca a acceptat sau nu invite-ul)
     * @return Lobby-ul sau mesajul erorii.
     */
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> joinLobby(@RequestBody JoinLobbyDTO lobbyDTO){
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

    /**
     * API POST pentru a iesi dintr-un lobby.
     * @param lobbyDTO (DTO-ul ce contine numele lobby-ului si numele utilizatorului)
     * @return Lobby-ul sau mesajul erorii.
     */
    @PostMapping("/kick")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> kickFromLobby(@RequestBody KickLobbyDTO lobbyDTO) {
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
