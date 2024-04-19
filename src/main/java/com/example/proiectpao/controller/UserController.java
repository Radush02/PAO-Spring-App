package com.example.proiectpao.controller;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.AssignRoleDTO;
import com.example.proiectpao.dtos.UserDTO;
import com.example.proiectpao.dtos.UserLoginDTO;
import com.example.proiectpao.dtos.UserRegisterDTO;
import com.example.proiectpao.exceptions.AlreadyExistsException;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.service.UserService.IUserService;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired private IUserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            CompletableFuture<User> u = userService.register(userRegisterDTO);
            return new ResponseEntity<>(u, HttpStatus.OK);
        } catch (AlreadyExistsException e) {
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
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/assignRole")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> assignRole(@RequestBody AssignRoleDTO userRoleDTO) {
        try {
            CompletableFuture<UserDTO> user = userService.assignRole(userRoleDTO);
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/downloadUser")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> downloadUser(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            CompletableFuture<Resource> user = userService.downloadUser(userLoginDTO.getUsername());
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/uploadStats/{user}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> uploadStats(
            @RequestParam MultipartFile file, @PathVariable String user) {
        try {
            CompletableFuture<Boolean> userStats = userService.uploadStats(user, file);
            return new ResponseEntity<>(userStats.get(), HttpStatus.OK);
        } catch (NonExistentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
