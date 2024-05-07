package com.example.proiectpao.repository;

import com.example.proiectpao.collection.Chat;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findAllBySenderNameAndReceiverName(String senderName, String receiverName);
}
