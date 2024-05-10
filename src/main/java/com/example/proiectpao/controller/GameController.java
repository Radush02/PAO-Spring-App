package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.userDTOs.UserLoginDTO;
import com.example.proiectpao.enums.Results;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.service.GameService.IGameService;
import com.example.proiectpao.utils.Pair;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
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

    /**
     * API POST pentru a ataca o echipa.<br>
     * Logica jocului este explicata in functia GameService.attackTeam.
     * @param userLoginDTO (DTO-ul ce contine numele liderului de echipa)
     * @param opponent numele liderului echipei ce va fi atacata
     * @return Rezultatul meciului sau mesajul erorii.
     * @see IGameService#attackTeam(String, String)
     */
    @PostMapping("/attackTeam/{opponent}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> attackTeam(
            @RequestBody UserLoginDTO userLoginDTO, @PathVariable String opponent) {
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

    /**
     * API GET pentru a exporta un joc multiplayer.<br>
     * Pentru ca nu putem trimite atat numele fisierului cat si fisierul efectiv, am retinut numele fisierului in header-ul Content-Disposition.
     * @param gameId id-ul jocului
     * @return Fisierul efectiv sau mesajul erorii.
     * @throws ExecutionException Daca task-ul a fost anulat.
     * @throws InterruptedException Daca task-ul a fost intrerupt.
     */
    @GetMapping("/exportMultiplayerGame/{gameId}")
    @CrossOrigin(
            origins = "http://localhost:4200",
            allowedHeaders = "Content-Disposition",
            exposedHeaders = "Content-Disposition")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> exportMultiplayerGame(@PathVariable String gameId)
            throws ExecutionException, InterruptedException {
        try {
            CompletableFuture<Pair<Resource, String>> results =
                    gameService.exportMultiplayerGame(gameId);
            HttpHeaders headers = new HttpHeaders();
            ContentDisposition contentDisposition =
                    ContentDisposition.builder("attachment")
                            .filename(results.get().getSecond())
                            .build();
            headers.setContentDisposition(contentDisposition);
            return ResponseEntity.ok().headers(headers).body(results.get().getFirst());
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * API GET pentru a prelua detaliile unui joc.
     * @param gameId id-ul jocului
     * @return Detaliile jocului sau mesajul erorii.
     * @throws ExecutionException Daca task-ul a fost anulat.
     * @throws InterruptedException Daca task-ul a fost intrerupt.
     */
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

    /**
     * API GET pentru a prelua jocurile multiplayer ale unui user.
     * @param username numele user-ului
     * @return O lista de jocuri 5v5 sau mesajul erorii.
     * @throws ExecutionException Daca task-ul a fost anulat.
     * @throws InterruptedException Daca task-ul a fost intrerupt.
     */
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

    /**
     * API POST pentru a importa un back-up al unui joc multiplayer.<br>
     * @param gameId id-ul jocului
     * @param file fisierul ce contine back-up-ul jocului
     * @return true sau mesajul erorii.
     * @throws ExecutionException Daca task-ul a fost anulat.
     * @throws InterruptedException Daca task-ul a fost intrerupt.
     */
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
