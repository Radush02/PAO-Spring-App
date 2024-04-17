package com.example.proiectpao.service.GameService;

import com.example.proiectpao.collection.Game;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.enums.Results;
import com.example.proiectpao.repository.GameRepository;
import com.example.proiectpao.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class GameService implements IGameService{
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public GameService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }
    /**
     * Returneaza rezultatul unui meci.<br>
     * Game logic: <br>
     * MR12 cu OT de 6 runde <br>
     * Se joaca 24 de runde si primul la 13 castiga. <br>
     * In caz de egalitate se joaca inca 6 runde pana cand se alege castigtor. <br>
     * Exemplu de 3 meciuri MR12 cu OT: <br>
     * <a href="https://www.hltv.org/matches/2370726/natus-vincere-vs-g2-pgl-cs2-major-copenhagen-2024">Navi vs G2 in semifinala CS2 PGL Copenhagen Major 2024 </a> <br>
     * <br>
     * Momentan logica jocului este sa se atace random: <br>
     * <ul>
     *     <li>Generez un nr random intre 1-6 pt a decide cine castiga o runda.</li>
     *     <li>In functie de diferenta dintre nr extrase, se atribuie stats. </li>
     *     <ul>
     *         <li>>=4 - One tap HS</li>
     *         <li>=3 - 3 hituri winner, 0 loser</li>
     *         <li>=2 - 3 hituri, 1 loser</li>
     *         <li>=1 - 4 hituri, 2 loser</li>
     *     </ul>
     * </ul>
     * <br>
     @param Player1 Numele jucatorului din echipa A
     @param Player2 Numele jucatorului din echipa B
     @return Rezultatul meciului
     */
    @Override
    @Async
    public CompletableFuture<Results> attack(String Player1, String Player2) {
        User attacker = userRepository.findByUsernameIgnoreCase(Player1);
        User defender = userRepository.findByUsernameIgnoreCase(Player2);
        if(attacker==null || defender==null)
            return CompletableFuture.completedFuture(null);
        Game game = new Game();
        game.setUserId(attacker.getUserId());
        game.setOpponentId(defender.getUserId());
        Random r = new Random();
        int no_rounds = 24,attacker_score=0,defender_score=0;
        while(no_rounds>0){
            int attacker_roll = r.nextInt(6)+1;
            int defender_roll = r.nextInt(6)+1;
            if(attacker_roll>defender_roll){
                attacker_score = attackHelper(attacker, defender, attacker_score, attacker_roll, defender_roll);
            }
            else if(attacker_roll<defender_roll){
                defender_score = attackHelper(defender, attacker, defender_score, defender_roll, attacker_roll);
            }
            else
                continue;
            no_rounds--;
            if(no_rounds==0 && attacker_score==defender_score)
                no_rounds+=6;
            if(attacker_score>12 || defender_score>12)
                break;
        }
        if(attacker_score>defender_score){
            game.setScore(attacker_score+"-"+defender_score);
            attacker.addWin();
            defender.addLoss();
            game.setResult(Results.Win);
        }
        else{
            game.setScore(attacker_score+"-"+defender_score);
            attacker.addLoss();
            defender.addWin();
            game.setResult(Results.Loss);
        }
        attacker.getGames().add(game);
        defender.getGames().add(game);
        userRepository.save(attacker);
        userRepository.save(defender);
        gameRepository.save(game);
        return CompletableFuture.completedFuture(game.getResult());
    }

    private int attackHelper(User attacker, User defender, int attacker_score, int attacker_roll, int defender_roll) {
        attacker_score++;
        attacker.addKill();
        defender.addDeath();
        switch (attacker_roll-defender_roll){
            case 1 -> {
                attacker.addHits(4);
                attacker.addHits(2);
            }
            case 2 -> {
                attacker.addHits(3);
                attacker.addHits(1);
            }
            case 3 -> attacker.addHits(3);
            default -> {
                attacker.addHits(1);
                attacker.addHeadshot();
            }
        }
        return attacker_score;
    }
}
