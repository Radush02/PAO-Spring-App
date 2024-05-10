package com.example.proiectpao.repository;

import com.example.proiectpao.collection.MultiplayerGame;
import com.example.proiectpao.collection.User;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MultiplayerGameRepository extends MongoRepository<MultiplayerGame, String> {
    MultiplayerGame findByGameId(String gameId);
    void deleteByGameId(String gameId);

}
