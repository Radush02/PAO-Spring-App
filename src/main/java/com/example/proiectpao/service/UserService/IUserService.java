package com.example.proiectpao.service.UserService;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.UserDTO;
import com.example.proiectpao.dtos.UserLoginDTO;
import com.example.proiectpao.dtos.UserRegisterDTO;

import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<User> register(UserRegisterDTO userRegisterDTO);

    CompletableFuture<UserDTO> login(UserLoginDTO userLoginDTO);
}
