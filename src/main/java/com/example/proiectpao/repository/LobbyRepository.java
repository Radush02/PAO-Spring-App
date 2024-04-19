package com.example.proiectpao.repository;

import com.example.proiectpao.collection.Lobby;
import com.example.proiectpao.collection.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends MongoRepository<Lobby, String> {
    Lobby findByLobbyLeader(User u);
}
