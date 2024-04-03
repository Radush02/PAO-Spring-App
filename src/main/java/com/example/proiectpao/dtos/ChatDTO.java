package com.example.proiectpao.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatDTO {
    private String message;
    private String senderId;
    private String receiverId;
}
