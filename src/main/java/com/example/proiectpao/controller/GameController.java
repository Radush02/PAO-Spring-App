package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.userDTOs.UserLoginDTO;
import com.example.proiectpao.enums.Results;
import com.example.proiectpao.service.GameService.IGameService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
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
        CompletableFuture<?> results =
                gameService.attackTeam(userLoginDTO.getUsername(), opponent);
        if (results == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(results.get(), HttpStatus.OK);
    }
}
