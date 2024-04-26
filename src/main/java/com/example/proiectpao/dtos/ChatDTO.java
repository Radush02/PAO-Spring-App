package com.example.proiectpao.dtos;

import lombok.Getter;
import lombok.Setter;

/*
   DTO-ul contine datele necesare pentru a trimite un mesaj.
*/
@Getter
@Setter
public class ChatDTO {
    private String message;
    private String senderName;
}
