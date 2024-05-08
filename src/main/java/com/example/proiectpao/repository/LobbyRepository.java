package com.example.proiectpao.repository;

import com.example.proiectpao.collection.Lobby;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LobbyRepository extends MongoRepository<Lobby, String> {
    Lobby findByLobbyLeader(String attacker);

    Lobby findByLobbyName(String lobbyName);

    @Query("{ 'players': ?0 }")
    Lobby findLobbyByPlayer(String username);

    @Query(value = "{}", fields = "{ 'lobbyLeader' : 1}")
    List<Lobby> getLobbyLeaders();
}
