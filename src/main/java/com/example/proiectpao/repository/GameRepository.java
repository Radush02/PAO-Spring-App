package com.example.proiectpao.repository;

import com.example.proiectpao.collection.SingleplayerGame;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<SingleplayerGame, String> {}
