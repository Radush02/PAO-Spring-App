package com.example.proiectpao.repository;

import com.example.proiectpao.collection.AdminLog;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminLogRepository extends MongoRepository<AdminLog, String> {

    public List<AdminLog> findAllByAdminAndUser(String admin, String user);
}
