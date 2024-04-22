package com.example.proiectpao.service.UserService;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.userDTOs.AssignRoleDTO;
import com.example.proiectpao.dtos.userDTOs.UserDTO;
import com.example.proiectpao.dtos.userDTOs.UserLoginDTO;
import com.example.proiectpao.dtos.userDTOs.UserRegisterDTO;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<User> register(UserRegisterDTO userRegisterDTO);

    CompletableFuture<UserDTO> login(UserLoginDTO userLoginDTO) throws IOException;

    CompletableFuture<UserDTO> displayUser(String username);

    CompletableFuture<UserDTO> assignRole(AssignRoleDTO userRoleDTO);

    CompletableFuture<String> downloadUser(String username) throws IOException;
}
