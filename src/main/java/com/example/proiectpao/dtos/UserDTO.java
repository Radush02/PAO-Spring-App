package com.example.proiectpao.dtos;

import com.example.proiectpao.enums.Role;
import lombok.Getter;
import lombok.Setter;

/*
   DTO-ul contine informatiile generale ale unui utilizator, ce vor fi afisate doar pentru
   administratori si utilizatorul respectiv
*/
@Setter
@Getter
public class UserDTO {
    private String userId;
    private String username;
    private String email;
    private String name;
    private Role role;
}
