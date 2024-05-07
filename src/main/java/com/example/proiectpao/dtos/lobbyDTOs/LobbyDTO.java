package com.example.proiectpao.dtos.lobbyDTOs;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class LobbyDTO {
    private String lobbyLeader;
    private String name;
    private List<String> players;
}
