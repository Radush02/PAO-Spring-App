package com.example.proiectpao.dtos.userDTOs;

import com.example.proiectpao.enums.Role;
import lombok.Getter;

@Getter
public class AssignRoleDTO {
    public String possibleAdminID;
    public String username;
    public Role role;
}