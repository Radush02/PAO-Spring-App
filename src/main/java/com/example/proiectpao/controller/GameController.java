package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.userDTOs.UserLoginDTO;
import com.example.proiectpao.enums.Results;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.service.GameService.IGameService;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:4200")
public class GameController {
    @Autowired private final IGameService gameService;

    public GameController(IGameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/attack/{opponent}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Results> attack(
            @RequestBody UserLoginDTO userLoginDTO, @PathVariable String opponent)
            throws ExecutionException, InterruptedException {
        CompletableFuture<Results> results =
                gameService.attack(userLoginDTO.getUsername(), opponent);
        if (results == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(results.get(), HttpStatus.OK);
    }
    @PostMapping("/attackTeam/{opponent}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> attackTeam(
            @RequestBody UserLoginDTO userLoginDTO, @PathVariable String opponent)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<?> results =
                    gameService.attackTeam(userLoginDTO.getUsername(), opponent);
            return new ResponseEntity<>(results.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/exportMultiplayerGame/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> exportMultiplayerGame(@PathVariable String gameId)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<?> results = gameService.exportMultiplayerGame(gameId);
            return new ResponseEntity<>(results.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getGame/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getGame(@PathVariable String gameId)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<?> results = gameService.getGame(gameId);
            return new ResponseEntity<>(results.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("displayMultiplayerGame/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> displayMultiplayerGame(@PathVariable String username)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<?> results = gameService.displayMultiplayerGame(username);
            return new ResponseEntity<>(results.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/importMultiplayerGame/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> importMultiplayerGame(
            @PathVariable String gameId, @RequestBody MultipartFile file)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<?> results = gameService.importMultiplayerGame(gameId, file);
            return new ResponseEntity<>(results.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
