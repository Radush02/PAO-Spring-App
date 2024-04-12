package com.example.proiectpao.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/*
   DTO-ul contine statisticile complete unui jucator
*/
@Getter
@AllArgsConstructor
@Builder
public class StatsDTO {
    private int wins;
    private int losses;
    private int kills;
    private int deaths;
    private int hits;
    private int headshots;
    private double WR;
    private double KDR;
    private double HSp;

    public static class StatsDTOBuilder {
        private int wins = 0;
        private int losses = 0;
        private int kills = 0;
        private int deaths = 0;
        private int hits = 0;
        private int headshots = 0;
        private double WR = 0.0;
        private double KDR = 0.0;
        private double HSp = 0.0;
    }
}
