package com.example.proiectpao.dtos.userDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String name;
}
