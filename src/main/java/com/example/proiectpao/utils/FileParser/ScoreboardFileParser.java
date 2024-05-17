package com.example.proiectpao.utils.FileParser;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.proiectpao.collection.MultiplayerGame;
import com.example.proiectpao.collection.MultiplayerUserStats;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.service.S3Service.S3Service;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.max;

@Component
public class ScoreboardFileParser extends FileParser {
    private static String repeat(int times) {
        return new String(new char[times]).replace("\0", " ");
    }

    @Override
    public boolean read(Object game, MultipartFile file, S3Service s3, Object type)
            throws IOException {
        try {
            String content = getFileContent(file);
            String s3Content = getS3FileContent(file.getOriginalFilename(), s3);
            if (!content.equals(s3Content))
                throw new IOException("Continutul back-up-ului a fost modificat!");

            MultiplayerGame multiplayerGame = (MultiplayerGame) game;
            String[] lines = content.split("\n");
            multiplayerGame.setGameId(lines[0]);
            String[] players = lines[1].split(" vs ");
            multiplayerGame.setAttackerCaptain(players[0]);
            multiplayerGame.setDefenderCaptain(players[1]);
            String[] lobbies = lines[2].split(" vs ");
            multiplayerGame.setAttackerLobbyName(lobbies[0]);
            multiplayerGame.setDefenderLobbyName(lobbies[1]);
            multiplayerGame.setScore(lines[3].substring(6));
            multiplayerGame.setUserStats(new HashMap<>());
            for (int i = 5; i < lines.length; i++) {
                String[] playerStats = lines[i].split("\\|");
                System.out.println(Arrays.toString(playerStats));
                MultiplayerUserStats stats =
                        MultiplayerUserStats.builder()
                                .kills(Integer.parseInt(playerStats[1].trim()))
                                .deaths(Integer.parseInt(playerStats[2].trim()))
                                .headshots(Integer.parseInt(playerStats[3].trim()))
                                .hits(Integer.parseInt(playerStats[5].trim()))
                                .win(Boolean.parseBoolean(playerStats[6].trim()))
                                .build();
                multiplayerGame.getUserStats().put(playerStats[0].trim(), stats);
            }
            return true;
        } catch (com.amazonaws.services.kms.model.NotFoundException e) {
            throw new NotFoundException("Nu exista fisierul in baza noastra de date.");
        } catch (com.amazonaws.SdkClientException e) {
            throw new NonExistentException(
                    "Eroare AWS. Verifica conexiunea la internet." + e.getMessage());
        }
    }

    /**
     * Metoda ScoreboardFileParser.write este folosita pentru a scrie un fisier de tip .sb
     * @param data datele care trebuie scrise
     * @param s3 S3
     * @return numele fisierului scris
     */
    @Override
    public String write(Object data, S3Service s3) throws IOException {
        try {
            MultiplayerGame multiplayerGame = (MultiplayerGame) data;
            sortUserStats(multiplayerGame);

            StringBuilder continut =
                    new StringBuilder(
                            multiplayerGame.getGameId()
                                    + "\n"
                                    + multiplayerGame.getAttackerCaptain()
                                    + " vs "
                                    + multiplayerGame.getDefenderCaptain()
                                    + "\n"
                                    + multiplayerGame.getAttackerLobbyName()
                                    + " vs "
                                    + multiplayerGame.getDefenderLobbyName()
                                    + "\n");
            continut.append("Scor: ").append(multiplayerGame.getScore()).append("\n");
            int nrSpatii = "Nume player".length();
            for (String u : multiplayerGame.getUserStats().keySet()) {
                nrSpatii = max(nrSpatii, u.length());
            }
            continut.append("Nume player")
                    .append(repeat(nrSpatii - "Nume player".length()))
                    .append(" | Kills | Deaths | Headshots | Score | Hits | Win\n");
            for (String u : multiplayerGame.getUserStats().keySet()) {
                continut.append(u)
                        .append(repeat(nrSpatii - u.length()))
                        .append(" | ")
                        .append(multiplayerGame.getUserStats().get(u).getKills())
                        .append(
                                repeat(
                                        5
                                                - String.valueOf(
                                                                multiplayerGame
                                                                        .getUserStats()
                                                                        .get(u)
                                                                        .getKills())
                                                        .length()))
                        .append(" | ")
                        .append(multiplayerGame.getUserStats().get(u).getDeaths())
                        .append(
                                repeat(
                                        6
                                                - String.valueOf(
                                                                multiplayerGame
                                                                        .getUserStats()
                                                                        .get(u)
                                                                        .getDeaths())
                                                        .length()))
                        .append(" | ")
                        .append(multiplayerGame.getUserStats().get(u).getHeadshots())
                        .append(
                                repeat(
                                        9
                                                - String.valueOf(
                                                                multiplayerGame
                                                                        .getUserStats()
                                                                        .get(u)
                                                                        .getHeadshots())
                                                        .length()))
                        .append(" | ")
                        .append(
                                multiplayerGame.getUserStats().get(u).getKills() * 2
                                        + multiplayerGame.getUserStats().get(u).getHeadshots()
                                                * 1.5)
                        .append(" | ")
                        .append(multiplayerGame.getUserStats().get(u).getHits())
                        .append(" | ")
                        .append(multiplayerGame.getUserStats().get(u).isWin())
                        .append("\n");
            }
            String fileName = generateFileName();
            File temp = createTempFile(continut.toString(), ".sb");
            FileInputStream input = new FileInputStream(temp);
            MultipartFile multipartFile = createMultipartFile(temp, input);
            s3.uploadFile(fileName + ".sb", multipartFile);
            temp.delete();
            return fileName;
        } catch (Exception e) {
            throw new IOException("Eroare la scrierea fisierului");
        }
    }

    private void sortUserStats(MultiplayerGame multiplayerGame) {
        Map<String, MultiplayerUserStats> sortedUserStats =
                multiplayerGame.getUserStats().entrySet().stream()
                        .sorted(
                                (e1, e2) -> {
                                    if (e1.getValue().getKills() * 2
                                                    + e1.getValue().getHeadshots() * 1.5
                                            != e2.getValue().getKills() * 2
                                                    + e2.getValue().getHeadshots() * 1.5) {
                                        return (int)
                                                (e2.getValue().getKills() * 2
                                                        + e2.getValue().getHeadshots() * 1.5
                                                        - e1.getValue().getKills() * 2
                                                        - e1.getValue().getHeadshots() * 1.5);
                                    }
                                    if (e1.getValue().getKills() != e2.getValue().getKills()) {
                                        return e2.getValue().getKills() - e1.getValue().getKills();
                                    }
                                    if (e1.getValue().getHeadshots()
                                            != e2.getValue().getHeadshots()) {
                                        return e2.getValue().getHeadshots()
                                                - e1.getValue().getHeadshots();
                                    }
                                    return e1.getKey().compareTo(e2.getKey());
                                })
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new));
        System.out.println(sortedUserStats);
        multiplayerGame.setUserStats(sortedUserStats);
    }
}
