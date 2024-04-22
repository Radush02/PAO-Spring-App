package com.example.proiectpao.repository;

import com.example.proiectpao.collection.Chat;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends GenericRepository<Chat, String> {
    public List<Chat> findAllBySenderIdAndReceiverId(String senderId, String receiverId);
}
