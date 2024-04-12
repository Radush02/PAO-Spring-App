package com.example.proiectpao.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class Stats {
    private int wins;
    private int losses;
    private int kills;
    private int deaths;
    private int hits;
    private int headshots;

    public static class StatsBuilder {
        private int wins = 0;
        private int losses = 0;
        private int kills = 0;
        private int deaths = 0;
        private int hits = 0;
        private int headshots = 0;
    }
}
