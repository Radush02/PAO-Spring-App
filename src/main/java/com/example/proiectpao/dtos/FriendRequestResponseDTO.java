package com.example.proiectpao.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestResponseDTO {
    private String sender;
    private String receiver;
    private boolean accepted;
}
