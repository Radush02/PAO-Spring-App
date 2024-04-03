package com.example.proiectpao.service.UserService;

import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.UserDTO;
import com.example.proiectpao.dtos.UserLoginDTO;
import com.example.proiectpao.dtos.UserRegisterDTO;

public interface IUserService {
    User register(UserRegisterDTO userRegisterDTO);

    UserDTO login(UserLoginDTO userLoginDTO);
}
