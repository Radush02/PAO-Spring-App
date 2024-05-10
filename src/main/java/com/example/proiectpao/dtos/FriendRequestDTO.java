package com.example.proiectpao.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FriendRequestDTO {
    private String sender;
    private String receiver;
}
