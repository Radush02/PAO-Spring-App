package com.example.proiectpao.controller;

import com.example.proiectpao.dtos.FriendRequestDTO;
import com.example.proiectpao.dtos.FriendRequestResponseDTO;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.service.FriendRequestsService.IFriendRequestService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Clasa controller pentru validarea prietenilor.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/friends")
public class FriendRequestController {
    @Autowired private final IFriendRequestService friendRequestService;

    public FriendRequestController(IFriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    /**
     * API POST pentru a trimite o cerere de prietenie.
     * @param friend (DTO-ul ce contine numele utilizatorului ce trimite cererea si numele utilizatorului ce o primeste)
     * @return true sau mesajul erorii.
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addFriendRequest(@RequestBody FriendRequestDTO friend) {
        try {
            CompletableFuture<Boolean> res = friendRequestService.addFriendRequest(friend);
            return new ResponseEntity<>(res.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a sterge o cerere de prietenie.
     * @param friend (DTO-ul ce contine numele utilizatorului ce trimite cererea si numele utilizatorului ce o primeste)
     * @return true sau mesajul erorii.
     */
    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteFriendRequest(@RequestBody FriendRequestDTO friend) {
        try {
            CompletableFuture<Boolean> res = friendRequestService.deleteFriendRequest(friend);
            return new ResponseEntity<>(res.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a raspunde la o cerere de prietenie.
     * @param friend (DTO-ul ce contine numele utilizatorului ce trimite cererea si numele utilizatorului ce o primeste si raspunsul)
     * @return true daca a acceptat, false daca a respins sau mesajul erorii.
     */
    @PostMapping("/response")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> friendRequestResponse(@RequestBody FriendRequestResponseDTO friend) {
        try {
            CompletableFuture<Boolean> res = friendRequestService.friendRequestResponse(friend);
            return new ResponseEntity<>(res.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a prelua cererile de prietenie trimise.
     * @param username numele utilizatorului
     * @return O lista de DTO-uri ce contine userii ce au primit cereri si numele utilizatorului sau mesajul erorii.
     */
    @GetMapping("/sent/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> sentRequests(@PathVariable String username) {
        try {
            CompletableFuture<List<FriendRequestDTO>> res =
                    friendRequestService.sentRequests(username);
            return new ResponseEntity<>(res.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a prelua cererile de prietenie primite.
     * @param username numele utilizatorului
     * @return O lista de DTO-uri ce contine userii ce au trimis cereri si numele utilizatorului sau mesajul erorii.
     */
    @GetMapping("/get/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getRequests(@PathVariable String username) {
        try {
            CompletableFuture<List<FriendRequestDTO>> res =
                    friendRequestService.getRequests(username);
            return new ResponseEntity<>(res.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
