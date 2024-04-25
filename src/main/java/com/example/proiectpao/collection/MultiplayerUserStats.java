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
    public static class MultiplayerUserStatsBuilder {
        private int kills = 0;
        private int deaths = 0;
        private int headshots = 0;
        private int hits = 0;
    }
}
