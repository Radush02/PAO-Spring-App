package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clasa User reprezinta un utilizator al aplicatiei.<br>
 * Un utilizator contine un id, rolul, numele de utilizator,
 * seed-ul, hash-ul, email-ul, numele, statistici, lista de jocuri jucate si lista de prieteni.
 */
@Data
@Builder
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class User {
    @Id private String userId;
    private Role role;
    private String username;
    private String seed;
    private String hash;
    private String email;
    private String name;
    private Stats stats;
    private List<String> gameIDs;
    private List<String> friends;

    public User() {
        this.role = Role.User;
        gameIDs = new ArrayList<>();
        friends = new ArrayList<>();
    }

    public void addHits(int i) {
        stats.setHits(stats.getHits() + i);
    }

    public void addHeadshot() {
        stats.setHeadshots(stats.getHeadshots() + 1);
    }

    public void addDeath() {
        stats.setDeaths(stats.getDeaths() + 1);
    }

    public void addKill() {
        stats.setKills(stats.getKills() + 1);
    }

    public void addWin() {
        stats.setWins(stats.getWins() + 1);
    }

    public void addLoss() {
        stats.setLosses(stats.getLosses() + 1);
    }

    public void addGame(String game) {
        gameIDs.add(game);
    }

    public void addFriend(String friend) {
        friends.add(friend);
    }
}
