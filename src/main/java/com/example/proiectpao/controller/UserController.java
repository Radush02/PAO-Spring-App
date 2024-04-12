package com.example.proiectpao.controller;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.AssignRoleDTO;
import com.example.proiectpao.dtos.UserDTO;
import com.example.proiectpao.dtos.UserLoginDTO;
import com.example.proiectpao.dtos.UserRegisterDTO;
import com.example.proiectpao.service.UserService.IUserService;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO)
            throws ExecutionException, InterruptedException, IOException {
        CompletableFuture<UserDTO> user = userService.login(userLoginDTO);
        if (user == null) {
            return new ResponseEntity<>(
                    "User inexistent sau parola gresita", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    @PostMapping("/assignRole")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> assignRole(@RequestBody AssignRoleDTO userRoleDTO) {
        CompletableFuture<UserDTO> user = userService.assignRole(userRoleDTO);
        if (user == null) {
            return new ResponseEntity<>("User inexistent sau rol gresit", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
