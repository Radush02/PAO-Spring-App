package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Results;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clasa SingleplayerGame reprezinta un joc jucat intre 2 utilizator.<br>
 * Un joc singleplayer contine un id, id-ul utilizatorului care a jucat, id-ul adversarului,
 * rezultatul jocului, scorul, data la care s-a jucat.
 * @Author Radu
 */
@Data
@Builder
@Document(collection = "games")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SingleplayerGame {
    private String gameId;
    private String userId;
    private String opponentId;
    private Results result;
    private String score;
    private Date date;
}
