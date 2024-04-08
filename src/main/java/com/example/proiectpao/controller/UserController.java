package com.example.proiectpao.controller;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.UserDTO;
import com.example.proiectpao.dtos.UserLoginDTO;
import com.example.proiectpao.dtos.UserRegisterDTO;
import com.example.proiectpao.service.UserService.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired private IUserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        CompletableFuture<User> u = userService.register(userRegisterDTO);
        if (u == null) {
            return new ResponseEntity<>(
                    "Exista deja un utilizator cu acel nume", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(u, HttpStatus.OK);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) throws ExecutionException, InterruptedException {
        CompletableFuture<UserDTO> user = userService.login(userLoginDTO);
        if (user == null) {
            return new ResponseEntity<>(
                    "User inexistent sau parola gresita", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }
}
