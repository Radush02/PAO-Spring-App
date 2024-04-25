package com.example.proiectpao.service.GameService;

import com.example.proiectpao.collection.*;
import com.example.proiectpao.enums.Results;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.repository.GameRepository;
import com.example.proiectpao.repository.LobbyRepository;
import com.example.proiectpao.repository.MultiplayerGameRepository;
import com.example.proiectpao.repository.UserRepository;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class GameService implements IGameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final MultiplayerGameRepository multiplayerGameRepository;

    public GameService(
            GameRepository gameRepository,
            UserRepository userRepository,
            LobbyRepository lobbyRepository, MultiplayerGameRepository multiplayerGameRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.multiplayerGameRepository = multiplayerGameRepository;
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
     * Logica jocului este sa se atace random: <br>
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
     * @param Player1 Numele jucatorului din echipa A
     * @param Player2 Numele jucatorului din echipa B
     * @return Rezultatul meciului
     */
    @Override
    @Async
    public CompletableFuture<Results> attack(String Player1, String Player2) {
        User attacker = userRepository.findByUsernameIgnoreCase(Player1);
        User defender = userRepository.findByUsernameIgnoreCase(Player2);
        if (attacker == null || defender == null) return CompletableFuture.completedFuture(null);
        SingleplayerGame singleplayerGame = new SingleplayerGame();
        singleplayerGame.setUserId(attacker.getUserId());
        singleplayerGame.setOpponentId(defender.getUserId());
        Random r = new Random();
        int no_rounds = 24, attacker_score = 0, defender_score = 0,no_ot=0;
        while (no_rounds > 0) {
            int attacker_roll = r.nextInt(6) + 1;
            int defender_roll = r.nextInt(6) + 1;
            if (attacker_roll > defender_roll) {
                attacker_score =
                        attackHelper(
                                attacker, defender, attacker_score, attacker_roll, defender_roll);
            } else if (attacker_roll < defender_roll) {
                defender_score =
                        attackHelper(
                                defender, attacker, defender_score, defender_roll, attacker_roll);
            } else continue;
            no_rounds--;
            if (no_rounds == 0 && attacker_score == defender_score){
                no_rounds += 6;
                no_ot++;
            }
            if (attacker_score>12+3*no_ot && defender_score<12+3*no_ot)break;
            if (defender_score>12+3*no_ot && attacker_score<12+3*no_ot)break;
        }
        if (attacker_score > defender_score) {
            singleplayerGame.setScore(attacker_score + "-" + defender_score);
            attacker.addWin();
            defender.addLoss();
            singleplayerGame.setResult(Results.Win);
        } else {
            singleplayerGame.setScore(attacker_score + "-" + defender_score);
            attacker.addLoss();
            defender.addWin();
            singleplayerGame.setResult(Results.Loss);
        }
        userRepository.save(attacker);
        userRepository.save(defender);
        gameRepository.save(singleplayerGame);
        return CompletableFuture.completedFuture(singleplayerGame.getResult());
    }

    @Override
    @Async
    public CompletableFuture<?> attackTeam(String attackerCaptain, String defenderCaptain) {
        User attacker = userRepository.findByUsernameIgnoreCase(attackerCaptain);
        Lobby lobbyAttacker = lobbyRepository.findByLobbyLeader(attacker.getUsername());
        if(lobbyAttacker == null)
            throw new NonExistentException("Echipa lui " + attacker.getUsername() + " nu exista!");
        if (lobbyAttacker.getPlayers().size() != 5) {
            throw new NonExistentException(
                    "Echipa lui " + attacker.getUsername() + " nu are 5 jucatori!");
        }
        User defender = userRepository.findByUsernameIgnoreCase(defenderCaptain);
        Lobby lobbyDefender = lobbyRepository.findByLobbyLeader(defender.getUsername());
        if(lobbyDefender == null)
            throw new NonExistentException("Echipa lui " + defender.getUsername() + " nu exista!");
        if (lobbyDefender.getPlayers().size() != 5) {
            throw new NonExistentException(
                    "Echipa lui " + defender.getUsername() + " nu are 5 jucatori!");
        }
        List<User> attackerCopy = new ArrayList<>();
        List<User> defenderCopy = new ArrayList<>();
        HashMap<String, Stats> gameStats= new HashMap<>();
        for(User u: lobbyAttacker.getPlayers()){
            gameStats.put(u.getUsername(),new Stats(u.getStats().getWins(),u.getStats().getLosses(),u.getStats().getKills(),u.getStats().getDeaths(),u.getStats().getHits(),u.getStats().getHeadshots()));
        }
        for(User u: lobbyDefender.getPlayers()){
            gameStats.put(u.getUsername(),new Stats(u.getStats().getWins(),u.getStats().getLosses(),u.getStats().getKills(),u.getStats().getDeaths(),u.getStats().getHits(),u.getStats().getHeadshots()));
        }
        int no_rounds = 24, attacker_score = 0, defender_score = 0,attackerRounds = 0,defenderRounds = 0,no_ot=0;
        while (no_rounds > 0 ) {
            //System.out.println(no_rounds);
            defenderCopy.clear();
            attackerCopy.clear();
            defenderCopy.addAll(lobbyDefender.getPlayers());
            attackerCopy.addAll(lobbyAttacker.getPlayers());
            Collections.shuffle(attackerCopy);
            Collections.shuffle(defenderCopy);
            while (!attackerCopy.isEmpty() && !defenderCopy.isEmpty()) {
                User attackerPlayer = attackerCopy.getFirst();
                User defenderPlayer = defenderCopy.getFirst();
                Random r = new Random();
                int attacker_roll = r.nextInt(6) + 1;
                int defender_roll = r.nextInt(6) + 1;
                if (attacker_roll > defender_roll) {
                    //System.out.println(attackerPlayer.getUsername() + " vs " + defenderPlayer.getUsername());
                    attacker_score =
                            attackHelper(
                                    attackerPlayer,
                                    defenderPlayer,
                                    attacker_score,
                                    attacker_roll,
                                    defender_roll);
                    defenderCopy.remove(defenderPlayer);
                } else if (attacker_roll < defender_roll) {
                    //System.out.println(attackerPlayer.getUsername() + " vs " + defenderPlayer.getUsername());
                    attackerCopy.remove(attackerPlayer);
                    defender_score =
                            attackHelper(
                                    defenderPlayer,
                                    attackerPlayer,
                                    defender_score,
                                    defender_roll,
                                    attacker_roll);
                }
            }
            no_rounds--;
            if(attackerCopy.isEmpty())
                defenderRounds++;
            else
                attackerRounds++;
            if (no_rounds == 0 && attackerRounds==defenderRounds) {
                no_rounds += 6;
                no_ot++;
            }
            if (attackerRounds > 12 + 3 * no_ot && defenderRounds < 12 + 3 * no_ot) break;
            if (defenderRounds > 12 + 3 * no_ot && attackerRounds < 12 + 3 * no_ot) break;

        }
        if(attackerRounds > defenderRounds) {
            for(User u: lobbyAttacker.getPlayers()){
                u.addWin();
                userRepository.save(u);
            }
            for(User u: lobbyDefender.getPlayers()){
                u.addLoss();
                userRepository.save(u);
            }
        } else {
            for(User u: lobbyDefender.getPlayers()){
                u.addWin();
                userRepository.save(u);
            }
            for(User u: lobbyAttacker.getPlayers()){
                u.addLoss();
                userRepository.save(u);
            }
        }
        HashMap<String,MultiplayerUserStats> results = new HashMap<>();
        fetchResults(lobbyAttacker, gameStats, results);
        fetchResults(lobbyDefender, gameStats, results);
        MultiplayerGame multiplayerGame = MultiplayerGame.builder().
                attackerCaptain(attacker).
                defenderCaptain(defender).
                result(attackerRounds>defenderRounds?Results.Win:Results.Loss).
                score(attackerRounds+"-"+defenderRounds).
                userStats(results).build();
        multiplayerGameRepository.save(multiplayerGame);
        return CompletableFuture.completedFuture(attackerRounds>defenderRounds?Results.Win:Results.Loss);
    }

    private void fetchResults(Lobby lobbyPlayer, HashMap<String, Stats> gameStats, HashMap<String, MultiplayerUserStats> results) {
        for(User u: lobbyPlayer.getPlayers()){
            results.put(u.getUsername(),MultiplayerUserStats.builder().
                    kills(u.getStats().getKills()-gameStats.get(u.getUsername()).getKills()).
                    deaths(u.getStats().getDeaths()-gameStats.get(u.getUsername()).getDeaths()).
                    headshots(u.getStats().getHeadshots()-gameStats.get(u.getUsername()).getHeadshots()).
                    hits(u.getStats().getHits()-gameStats.get(u.getUsername()).getHits()).build());
        }
    }

    private int attackHelper(
            User attacker,
            User defender,
            int attacker_score,
            int attacker_roll,
            int defender_roll) {
        attacker_score++;
        attacker.addKill();
        defender.addDeath();
        switch (attacker_roll - defender_roll) {
            case 1 -> {
                attacker.addHits(4);
                defender.addHits(2);
            }
            case 2 -> {
                attacker.addHits(3);
                defender.addHits(1);
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
