package com.example.proiectpao.collection;

import lombok.*;

/**
 * Clasa MultiplayerUserStats reprezinta statisticile unui utilizator in cadrul unui joc multiplayer.<br>
 * Statisticile contin numarul de kills, numarul de deaths, numarul de headshots, numarul de hits si daca a castigat sau nu.
 * @Author Radu
 */
@Setter
@Getter
@AllArgsConstructor
@Builder
public class MultiplayerUserStats {
    private int kills;
    private int deaths;
    private int headshots;
    private int hits;
    private boolean win;
}
