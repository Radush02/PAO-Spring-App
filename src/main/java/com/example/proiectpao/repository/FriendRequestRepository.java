package com.example.proiectpao.repository;

import com.example.proiectpao.collection.FriendRequest;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {
    FriendRequest findBySenderAndReceiver(String sender, String receiver);

    List<FriendRequest> findAllByReceiver(String username);

    List<FriendRequest> findAllBySender(String username);
}
