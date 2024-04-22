package com.example.proiectpao.dtos.userDTOs;

import lombok.Getter;

@Getter
/*
      DTO-ul contine datele necesare pentru inregistrare
*/
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String name;
}
