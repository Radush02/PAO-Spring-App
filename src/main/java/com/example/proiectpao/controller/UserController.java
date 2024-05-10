package com.example.proiectpao.controller;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.userDTOs.UserDTO;
import com.example.proiectpao.dtos.userDTOs.UserLoginDTO;
import com.example.proiectpao.dtos.userDTOs.UserRegisterDTO;
import com.example.proiectpao.exceptions.AlreadyExistsException;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.service.UserService.IUserService;
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

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired private IUserService userService;

    /**
     * API POST pentru a inregistra un utilizator.
     * @param userRegisterDTO (DTO-ul ce contine username-ul, parola si email-ul utilizatorului)
     * @return Utilizatorul creat sau mesajul erorii.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            CompletableFuture<User> u = userService.register(userRegisterDTO);
            return new ResponseEntity<>(u.get(), HttpStatus.OK);
        } catch (AlreadyExistsException | NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a loga un utilizator.
     * @param userLoginDTO (DTO-ul ce contine username-ul si parola utilizatorului)
     * @return Utilizatorul logat sau mesajul erorii.
     * @see IUserService#login(UserLoginDTO)
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            CompletableFuture<UserDTO> user = userService.login(userLoginDTO);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException | UnauthorizedActionException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a prelua prietenii unui utilizator.
     * @param username numele utilizatorului
     * @return Lista cu numele prietenilor sau mesajul erorii.
     */
    @GetMapping("/friends/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getFriends(@PathVariable String username) {
        try {
            CompletableFuture<List<String>> user = userService.getFriends(username);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a descarca un fisier JSON cu informatiile despre un utilizator.
     * Pentru ca nu putem trimite atat numele fisierului cat si fisierul efectiv, am retinut numele fisierului in header-ul Content-Disposition.
     * @param username numele utilizatorului
     * @return Fisierul cu informatiile sau mesajul erorii.
     */
    @GetMapping("/downloadUser/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin(
            origins = "http://localhost:4200",
            allowedHeaders = "Content-Disposition",
            exposedHeaders = "Content-Disposition")
    public ResponseEntity<?> downloadUser(@PathVariable String username) {
        try {
            CompletableFuture<Pair<Resource, String>> user = userService.downloadUser(username);
            HttpHeaders headers = new HttpHeaders();
            ContentDisposition contentDisposition =
                    ContentDisposition.builder("attachment")
                            .filename(user.get().getSecond())
                            .build();
            headers.setContentDisposition(contentDisposition);
            return ResponseEntity.ok().headers(headers).body(user.get().getFirst());
        } catch (NonExistentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API POST pentru a incarca statisticile unui utilizator.
     * @param file fisierul JSON cu statisticile
     * @param user numele utilizatorului
     * @return true daca s-a incarcat cu succes, false altfel sau mesajul erorii.
     */
    @PostMapping("/uploadStats/{user}")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(
            origins = "http://localhost:4200",
            allowedHeaders = "Content-Disposition",
            exposedHeaders = "Content-Disposition")
    public ResponseEntity<?> uploadStats(
            @RequestParam MultipartFile file, @PathVariable String user) {
        try {
            CompletableFuture<Boolean> userStats = userService.uploadStats(user, file);
            return new ResponseEntity<>(userStats.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API GET pentru a afisa informatiile unui utilizator.
     * @param username numele utilizatorului
     * @return DTO-ul cu informatiile utilizatorului sau mesajul erorii.
     */
    @GetMapping("/displayUser/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> displayUser(@PathVariable String username) {
        try {
            CompletableFuture<UserDTO> user = userService.displayUser(username);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
