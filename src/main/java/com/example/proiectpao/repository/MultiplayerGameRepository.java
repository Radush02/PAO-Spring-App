package com.example.proiectpao.repository;

import com.example.proiectpao.collection.MultiplayerGame;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MultiplayerGameRepository extends MongoRepository<MultiplayerGame, String> {
}
