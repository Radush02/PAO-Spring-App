package com.example.proiectpao.service.GameService;

import com.example.proiectpao.collection.*;
import com.example.proiectpao.enums.Penalties;
import com.example.proiectpao.enums.Results;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.exceptions.UnauthorizedActionException;
import com.example.proiectpao.repository.*;
import com.example.proiectpao.service.S3Service.S3Service;
import com.example.proiectpao.utils.FileParser.FileParser;
import com.example.proiectpao.utils.FileParser.ScoreboardFileParser;
import com.example.proiectpao.utils.Pair;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GameService implements IGameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final MultiplayerGameRepository multiplayerGameRepository;
    private final PunishRepository punishRepository;
    private final ScoreboardFileParser scoreboardFileParser;
    private final S3Service s3Service;

    public GameService(
            GameRepository gameRepository,
            UserRepository userRepository,
            LobbyRepository lobbyRepository,
            MultiplayerGameRepository multiplayerGameRepository,
            PunishRepository punishRepository,
            ScoreboardFileParser scoreboardFileParser,
            S3Service s3Service) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.multiplayerGameRepository = multiplayerGameRepository;
        this.punishRepository = punishRepository;
        this.scoreboardFileParser = FileParser.getInstance(ScoreboardFileParser.class);
        this.s3Service = s3Service;
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
        int no_rounds = 24, attacker_score = 0, defender_score = 0, no_ot = 0;
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
            if (no_rounds == 0 && attacker_score == defender_score) {
                no_rounds += 6;
                no_ot++;
            }
            if (attacker_score > 12 + 3 * no_ot && defender_score < 12 + 3 * no_ot) break;
            if (defender_score > 12 + 3 * no_ot && attacker_score < 12 + 3 * no_ot) break;
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
        if (lobbyAttacker == null)
            throw new NonExistentException("Echipa lui " + attacker.getUsername() + " nu exista!");
        if (!punishRepository
                .findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        attacker.getUserId(), Penalties.Ban, new Date())
                .isEmpty()) {
            lobbyRepository.delete(lobbyAttacker);
            throw new UnauthorizedActionException(
                    attacker.getUsername() + ", esti banat, nu poti juca. Lobby-ul a fost sters.");
        }
        if (lobbyAttacker.getPlayers().size() != 5) {
            throw new NonExistentException(
                    "Echipa lui " + attacker.getUsername() + " nu are 5 jucatori!");
        }
        User defender = userRepository.findByUsernameIgnoreCase(defenderCaptain);
        Lobby lobbyDefender = lobbyRepository.findByLobbyLeader(defender.getUsername());
        if (lobbyDefender == null)
            throw new NonExistentException("Echipa lui " + defender.getUsername() + " nu exista!");
        if (!punishRepository
                .findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                        attacker.getUserId(), Penalties.Ban, new Date())
                .isEmpty()) {
            lobbyRepository.delete(lobbyDefender);
            throw new UnauthorizedActionException(
                    defender.getUsername() + ", esti banat, nu poti juca. Lobby-ul a fost sters.");
        }
        if (lobbyDefender.getPlayers().size() != 5) {
            throw new NonExistentException(
                    "Echipa lui " + defender.getUsername() + " nu are 5 jucatori!");
        }
        if (lobbyAttacker.getLobbyName().equals(lobbyDefender.getLobbyName())) {
            throw new UnauthorizedActionException("Nu te poti ataca!");
        }

        List<User> attackerArray = new ArrayList<>();
        List<User> defenderArray = new ArrayList<>();
        for (String username : lobbyDefender.getPlayers()) {
            User user = userRepository.findByUsernameIgnoreCase(username);
            defenderArray.add(user);
        }
        for (String username : lobbyAttacker.getPlayers()) {
            User user = userRepository.findByUsernameIgnoreCase(username);
            attackerArray.add(user);
        }
        List<User> attackerCopy = new ArrayList<>();
        List<User> defenderCopy = new ArrayList<>();
        HashMap<String, MultiplayerUserStats> gameStats = new HashMap<>();
        checkPunish(lobbyAttacker, attackerArray, gameStats);
        checkPunish(lobbyDefender, defenderArray, gameStats);
        int no_rounds = 24,
                attacker_score = 0,
                defender_score = 0,
                attackerRounds = 0,
                defenderRounds = 0,
                no_ot = 0;
        while (no_rounds > 0) {
            // System.out.println(no_rounds);
            defenderCopy.clear();
            attackerCopy.clear();
            attackerCopy.addAll(attackerArray);
            defenderCopy.addAll(defenderArray);
            Collections.shuffle(attackerCopy);
            Collections.shuffle(defenderCopy);
            while (!attackerCopy.isEmpty() && !defenderCopy.isEmpty()) {
                User attackerPlayer = attackerCopy.getFirst();
                User defenderPlayer = defenderCopy.getFirst();
                Random r = new Random();
                int attacker_roll = r.nextInt(6) + 1;
                int defender_roll = r.nextInt(6) + 1;
                if (attacker_roll > defender_roll) {
                    attacker_score =
                            attackHelper(
                                    attackerPlayer,
                                    defenderPlayer,
                                    attacker_score,
                                    attacker_roll,
                                    defender_roll);
                    defenderCopy.remove(defenderPlayer);
                } else if (attacker_roll < defender_roll) {
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
            if (attackerCopy.isEmpty()) defenderRounds++;
            else attackerRounds++;
            if (no_rounds == 0 && attackerRounds == defenderRounds) {
                no_rounds += 6;
                no_ot++;
            }
            if (attackerRounds > 12 + 3 * no_ot && defenderRounds < 12 + 3 * no_ot) break;
            if (defenderRounds > 12 + 3 * no_ot && attackerRounds < 12 + 3 * no_ot) break;
        }
        addStats(defenderArray, gameStats);
        addStats(attackerArray, gameStats);
        MultiplayerGame multiplayerGame =
                MultiplayerGame.builder()
                        .gameId(UUID.randomUUID().toString())
                        .attackerCaptain(attacker.getUsername())
                        .defenderCaptain(defender.getUsername())
                        .attackerLobbyName(lobbyAttacker.getLobbyName())
                        .defenderLobbyName(lobbyDefender.getLobbyName())
                        .result(attackerRounds > defenderRounds ? Results.Win : Results.Loss)
                        .score(attackerRounds + "-" + defenderRounds)
                        .userStats(gameStats)
                        .build();

        if (attackerRounds > defenderRounds) {
            addGameResult(lobbyAttacker, lobbyDefender, multiplayerGame);
            for (User u : attackerArray) {
                gameStats.get(u.getUsername()).setWin(true);
            }
        } else {
            addGameResult(lobbyDefender, lobbyAttacker, multiplayerGame);
            for (User u : defenderArray) {
                gameStats.get(u.getUsername()).setWin(true);
            }
        }
        multiplayerGameRepository.save(multiplayerGame);

        return CompletableFuture.completedFuture(multiplayerGame.getGameId());
    }

    private void checkPunish(
            Lobby lobby, List<User> users, HashMap<String, MultiplayerUserStats> gameStats) {
        for (User u : users) {

            if (!punishRepository
                    .findAllByUserIDAndSanctionAndExpiryDateIsAfter(
                            u.getUserId(), Penalties.Ban, new Date())
                    .isEmpty()) {
                lobbyRepository.delete(lobby);
                throw new UnauthorizedActionException(
                        u.getUsername() + ", esti banat, nu poti juca. Lobby-ul a fost sters.");
            }
            gameStats.put(
                    u.getUsername(),
                    new MultiplayerUserStats(
                            u.getStats().getKills(),
                            u.getStats().getDeaths(),
                            u.getStats().getHeadshots(),
                            u.getStats().getHits(),
                            false));
        }
    }

    private void addStats(
            List<User> defenderArray, HashMap<String, MultiplayerUserStats> gameStats) {
        for (User u : defenderArray) {
            gameStats.put(
                    u.getUsername(),
                    MultiplayerUserStats.builder()
                            .kills(
                                    u.getStats().getKills()
                                            - gameStats.get(u.getUsername()).getKills())
                            .hits(u.getStats().getHits() - gameStats.get(u.getUsername()).getHits())
                            .headshots(
                                    u.getStats().getHeadshots()
                                            - gameStats.get(u.getUsername()).getHeadshots())
                            .deaths(
                                    u.getStats().getDeaths()
                                            - gameStats.get(u.getUsername()).getDeaths())
                            .build());
            System.out.println(
                    u.getUsername()
                            + " "
                            + u.getStats().getKills()
                            + " "
                            + u.getStats().getDeaths()
                            + " "
                            + u.getStats().getHits()
                            + " "
                            + u.getStats().getHeadshots());
            System.out.println(
                    gameStats.get(u.getUsername()).getKills()
                            + " "
                            + gameStats.get(u.getUsername()).getDeaths()
                            + " "
                            + gameStats.get(u.getUsername()).getHits()
                            + " "
                            + gameStats.get(u.getUsername()).getHeadshots());
        }
    }

    @Override
    @Async
    public CompletableFuture<Pair<Resource, String>> exportMultiplayerGame(String gameId) {
        MultiplayerGame multiplayerGame = multiplayerGameRepository.findByGameId(gameId);
        if (multiplayerGame == null) {
            throw new NonExistentException("Meciul nu exista");
        }

        try {
            String fileName = scoreboardFileParser.write(multiplayerGame, s3Service);
            return CompletableFuture.completedFuture(
                    new Pair<>(
                            new InputStreamResource(
                                    s3Service.getFile(fileName + ".sb").getObjectContent()),
                            fileName + ".sb"));

        } catch (Exception e) {
            throw new NonExistentException("Eroare la scrierea fisierului");
        }
    }

    @Override
    @Async
    public CompletableFuture<?> displayMultiplayerGame(String username) {
        User u = userRepository.findByUsernameIgnoreCase(username);
        if (u == null) {
            throw new NonExistentException("Utilizatorul nu exista");
        }
        List<String> games = u.getGameIDs();
        List<MultiplayerGame> multiplayerGames = new ArrayList<>();
        for (String gameId : games) {
            MultiplayerGame multiplayerGame = multiplayerGameRepository.findByGameId(gameId);
            if (multiplayerGame != null) {
                multiplayerGames.add(multiplayerGame);
            }
        }
        return CompletableFuture.completedFuture(multiplayerGames);
    }

    @Override
    @Async
    public CompletableFuture<?> getGame(String gameId) {
        MultiplayerGame multiplayerGame = multiplayerGameRepository.findByGameId(gameId);
        if (multiplayerGame == null) {
            throw new NonExistentException("Meciul nu exista");
        }
        return CompletableFuture.completedFuture(multiplayerGame);
    }

    @Override
    @Async
    public CompletableFuture<?> importMultiplayerGame(String gameId, MultipartFile file)
            throws IOException {
        MultiplayerGame multiplayerGame = multiplayerGameRepository.findByGameId(gameId);
        if (scoreboardFileParser.read(multiplayerGame, file, s3Service)) {
            multiplayerGameRepository.save(multiplayerGame);
            return CompletableFuture.completedFuture(multiplayerGame);
        }
        throw new NonExistentException("Eroare la citirea fisierului");
    }

    private void addGameResult(Lobby winner, Lobby loser, MultiplayerGame multiplayerGame) {
        for (String w : winner.getPlayers()) {
            User u = userRepository.findByUsernameIgnoreCase(w);
            u.addWin();
            u.setStats(
                    Stats.builder()
                            .wins(u.getStats().getWins())
                            .losses(u.getStats().getLosses())
                            .kills(
                                    u.getStats().getKills()
                                            + multiplayerGame.getUserStats().get(w).getKills())
                            .hits(
                                    u.getStats().getHits()
                                            + multiplayerGame.getUserStats().get(w).getHits())
                            .headshots(
                                    u.getStats().getHeadshots()
                                            + multiplayerGame.getUserStats().get(w).getHeadshots())
                            .deaths(
                                    u.getStats().getDeaths()
                                            + multiplayerGame.getUserStats().get(w).getDeaths())
                            .build());
            u.addGame(multiplayerGame.getGameId());
            userRepository.save(u);
        }
        for (String w : loser.getPlayers()) {
            User u = userRepository.findByUsernameIgnoreCase(w);
            u.addLoss();
            u.setStats(
                    Stats.builder()
                            .wins(u.getStats().getWins())
                            .losses(u.getStats().getLosses())
                            .kills(
                                    u.getStats().getKills()
                                            + multiplayerGame.getUserStats().get(w).getKills())
                            .hits(
                                    u.getStats().getHits()
                                            + multiplayerGame.getUserStats().get(w).getHits())
                            .headshots(
                                    u.getStats().getHeadshots()
                                            + multiplayerGame.getUserStats().get(w).getHeadshots())
                            .deaths(
                                    u.getStats().getDeaths()
                                            + multiplayerGame.getUserStats().get(w).getDeaths())
                            .build());
            u.addGame(multiplayerGame.getGameId());
            userRepository.save(u);
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
