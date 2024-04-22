package com.example.proiectpao.repository;

import com.example.proiectpao.collection.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GenericRepository<User, String> {
    User findByUsernameIgnoreCase(String username);
}
