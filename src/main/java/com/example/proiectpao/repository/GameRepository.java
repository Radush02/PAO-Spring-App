package com.example.proiectpao.repository;

import com.example.proiectpao.collection.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {}
