package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Results;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.Map;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "5v5Games")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MultiplayerGame {
    private String gameId;
    private String attackerCaptain;
    private String attackerLobbyName;
    private String defenderCaptain;
    private String defenderLobbyName;
    private Results result;
    private String score;
    private Date date;
    private Map<String, MultiplayerUserStats> userStats;
}
