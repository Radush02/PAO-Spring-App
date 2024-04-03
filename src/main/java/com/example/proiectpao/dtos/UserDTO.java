package com.example.proiectpao.dtos;

import com.example.proiectpao.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {
    private String username;
    private String email;
    private String name;
    private Role role;

}

