package com.example.proiectpao.collection;

import lombok.*;

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
