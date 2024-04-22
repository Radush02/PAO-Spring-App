package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Results;
import java.util.Date;
import java.util.List;

/*
 Denumiti attacker & defender pt a urmari mai bn actiunile
 Pt a evita teamA & teamB sau alte denumiri generice
*/
public class MultiplayerGame {
    private User attackerCaptain;
    private User defenderCaptain;
    private Results result;
    private String score;
    private Date date;
    private List<MultiplayerUserStats> userStats;
}
