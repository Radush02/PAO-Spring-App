package com.example.proiectpao.repository;

import com.example.proiectpao.collection.Lobby;
import com.example.proiectpao.collection.User;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends GenericRepository<Lobby, String> {
    Lobby findByLobbyLeader(User u);
}
