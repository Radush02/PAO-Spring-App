package com.example.proiectpao.repository;

import com.example.proiectpao.collection.Punish;
import com.example.proiectpao.enums.Penalties;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface PunishRepository extends GenericRepository<Punish, String> {
    Punish findByUserID(String userID);

    List<Punish> findAllByUserID(String userID);

    List<Punish> findAllByUserIDAndSanction(String userID, Penalties sanction);

    Punish findByUserIDAndSanction(String userID, Penalties sanction);
}
