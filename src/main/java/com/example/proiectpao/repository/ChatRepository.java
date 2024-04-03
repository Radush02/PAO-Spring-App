package com.example.proiectpao.repository;

import com.example.proiectpao.collection.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<Chat,String> {
}
