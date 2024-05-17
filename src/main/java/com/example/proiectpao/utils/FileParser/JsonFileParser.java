package com.example.proiectpao.utils.FileParser;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.proiectpao.collection.Chat;
import com.example.proiectpao.collection.Stats;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.userDTOs.ExportDTO;
import com.example.proiectpao.dtos.userDTOs.StatsDTO;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.repository.ChatRepository;
import com.example.proiectpao.repository.UserRepository;
import com.example.proiectpao.service.S3Service.S3Service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class JsonFileParser extends FileParser {

    @Override
    public boolean read(Object user, MultipartFile file, S3Service s3, Object type) {
        try {

            String s3Json = getS3FileContent(file.getOriginalFilename(), s3);
            String json = getFileContent(file);
            if (!json.equals(s3Json))
                throw new NonExistentException("Continutul back-up-ului a fost modificat!");

            parseAndSaveData(json, user, type);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (com.amazonaws.services.kms.model.NotFoundException e) {
            throw new NotFoundException("Nu exista fisierul in baza noastra de date.");
        } catch (com.amazonaws.SdkClientException e) {
            throw new NonExistentException(
                    "Eroare AWS. Verifica conexiunea la internet." + e.getMessage());
        }
        return true;
    }

    /**
     * Metoda JsonFileParser.write este folosita pentru a scrie un fisier JSON
     * @param userJson datele care trebuie scrise
     * @param s3 S3
     * @return numele fisierului scris
     */
    @Override
    public String write(Object userJson, S3Service s3) throws IOException {
        String fileName = generateFileName();
        File tempFile = createTempFile(userJson,".json");

        try (FileInputStream input = new FileInputStream(tempFile)) {
            MultipartFile multipartFile = createMultipartFile(tempFile, input);
            s3.uploadFile(fileName + ".json", multipartFile);
        } finally {
            tempFile.delete();
        }

        return fileName;
    }



    private void parseAndSaveData(String json, Object user, Object type) {
        if (type instanceof UserRepository) {
            saveUserData(json, user, (UserRepository) type);
        } else if (type instanceof ChatRepository) {
            saveChatData(json, (ChatRepository) type);
        }
    }


    private void saveUserData(String json, Object user, UserRepository userRepository) {
        ExportDTO exportDTO = new Gson().fromJson(json, ExportDTO.class);
        User u = (User) user;

        if (!Objects.equals(exportDTO.getUserDTO().getName(), u.getName())) {
            throw new NonExistentException("Numele userului nu corespunde cu cel din fisier.");
        }

        u.setStats(convertToStats(exportDTO.getUserDTO().getStats()));
        u.setGameIDs(exportDTO.getGameIDs());
        userRepository.save(u);
    }

    private Stats convertToStats(StatsDTO stats) {
        return Stats.builder()
                .kills(stats.getKills())
                .deaths(stats.getDeaths())
                .wins(stats.getWins())
                .losses(stats.getLosses())
                .headshots(stats.getHeadshots())
                .hits(stats.getHits())
                .build();
    }

    private void saveChatData(String json, ChatRepository chatRepository) {
        Type listType = new TypeToken<List<Chat>>() {}.getType();
        List<Chat> chats = new Gson().fromJson(json, listType);
        chatRepository.saveAll(chats);
    }




}
