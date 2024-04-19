package com.example.proiectpao.dtos;

import lombok.Getter;

@Getter
public class JoinLobbyDTO {
    private String lobbyLeader;
    private String username;
    private boolean acceptedInvite;
}
