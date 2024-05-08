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
