package com.example.proiectpao.service.LobbyService;

import com.example.proiectpao.collection.Lobby;
import com.example.proiectpao.dtos.CreateLobbyDTO;
import com.example.proiectpao.dtos.JoinLobbyDTO;
import java.util.concurrent.CompletableFuture;

import com.example.proiectpao.dtos.KickLobbyDTO;
import org.springframework.scheduling.annotation.Async;

public interface ILobbyService {

    @Async
    CompletableFuture<Lobby> createLobby(CreateLobbyDTO lobbyDTO);

    @Async
    CompletableFuture<Lobby> joinLobby(JoinLobbyDTO lobbyDTO);

    @Async
    CompletableFuture<Lobby> kickFromLobby(KickLobbyDTO lobbyDTO);
}
