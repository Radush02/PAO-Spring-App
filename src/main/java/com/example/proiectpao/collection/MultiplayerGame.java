package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Results;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/*
 Denumiti attacker & defender pt a urmari mai bn actiunile
 Pt a evita teamA & teamB sau alte denumiri generice
*/

@Data
@Builder
@Document(collection = "5v5Games")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MultiplayerGame {
    private User attackerCaptain;
    private User defenderCaptain;
    private Results result;
    private String score;
    private Date date;
    private HashMap<String,MultiplayerUserStats> userStats;
    public static class MultiplayerGameBuilder {
        private User attackerCaptain;
        private User defenderCaptain;
        private Results result;
        private String score;
        private Date date = new Date();
        private HashMap<String,MultiplayerUserStats> userStats;
    }
}
