package com.example.proiectpao.service.LobbyService;

import com.example.proiectpao.collection.Lobby;
import com.example.proiectpao.dtos.lobbyDTOs.CreateLobbyDTO;
import com.example.proiectpao.dtos.lobbyDTOs.JoinLobbyDTO;
import com.example.proiectpao.dtos.lobbyDTOs.KickLobbyDTO;
import com.example.proiectpao.dtos.lobbyDTOs.LobbyDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

public interface ILobbyService {

    /**
     * Metoda getLobbies returneaza toate lobby-urile existente.
     * @return Lista de lobby-uri.
     */
    @Async
    CompletableFuture<List<LobbyDTO>> getLobbies();

    /**
     * Metoda inLobby verifica daca un utilizator este intr-un lobby.
     * @param username numele utilizatorului
     * @return Numele lobby-ului in care se afla utilizatorul.
     */
    @Async
    CompletableFuture<String> inLobby(String username);

    /**
     * Metoda inLobby verifica daca un utilizator este intr-un lobby.
     * @param lobby numele lobby-ului
     * @param username numele utilizatorului
     * @return true daca utilizatorul este in lobby, false altfel.
     */
    @Async
    CompletableFuture<Boolean> inLobby(String lobby, String username);

    /**
     * Metoda createLobby creeaza un lobby.
     * @param lobbyDTO (DTO-ul ce contine numele lobby-ului si numele liderului)
     * @return Lobby-ul creat.
     */
    @Async
    CompletableFuture<Lobby> createLobby(CreateLobbyDTO lobbyDTO);

    /**
     * Metoda joinLobby adauga un utilizator intr-un lobby.
     * @param lobbyDTO (DTO-ul ce contine numele lobby-ului si numele utilizatorului)
     * @return Lobby-ul in care s-a adaugat utilizatorul.
     */
    @Async
    CompletableFuture<Lobby> joinLobby(JoinLobbyDTO lobbyDTO);

    /**
     * Metoda getLobbyLeaders returneaza liderii de lobby-uri.
     * @return Lista cu liderii de lobby-uri.
     */
    @Async
    CompletableFuture<List<String>> getLobbyLeaders();

    /**
     * Metoda kickFromLobby scoate un utilizator dintr-un lobby.
     * @param lobbyDTO (DTO-ul ce contine numele liderului si numele utilizatorului)
     * @return Lobby-ul din care s-a scos utilizatorul.
     */
    @Async
    CompletableFuture<Lobby> kickFromLobby(KickLobbyDTO lobbyDTO);
}
