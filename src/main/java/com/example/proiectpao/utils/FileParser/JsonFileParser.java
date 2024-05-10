package com.example.proiectpao.utils.FileParser;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.model.S3Object;
import com.example.proiectpao.collection.Chat;
import com.example.proiectpao.collection.Stats;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.MessageDTO;
import com.example.proiectpao.dtos.MessageExportDTO;
import com.example.proiectpao.dtos.userDTOs.ExportDTO;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.repository.ChatRepository;
import com.example.proiectpao.repository.UserRepository;
import com.example.proiectpao.service.S3Service.S3Service;
import com.example.proiectpao.utils.RandomGenerator.RandomNameGenerator;
import com.google.gson.Gson;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class JsonFileParser extends FileParser {


    @Override
    public boolean read(Object user, MultipartFile file, S3Service s3,Object type) {
        try {

            InputStream is = file.getInputStream();
            S3Object s3obj = s3.getFile(file.getOriginalFilename());
            System.out.println("ok");
            String json = IOUtils.toString(is, StandardCharsets.UTF_8);
            String s3Json = IOUtils.toString(s3obj.getObjectContent(), StandardCharsets.UTF_8);
            if (!json.equals(s3Json)) {
                System.out.println(json);
                System.out.println(s3Json);
                //System.out.println("Continutul back-up-ului a fost modificat!");
                throw new NonExistentException("Continutul back-up-ului a fost modificat!");
            }

            if(type instanceof UserRepository){
                ExportDTO u = new Gson().fromJson(json, ExportDTO.class);
                User k = (User) user;
                if (!Objects.equals(u.getUserDTO().getName(), k.getName())) {
                    System.out.println(u.getUserDTO().getName() + " " + k.getName());
                    throw new NonExistentException("Numele userului nu corespunde cu cel din fisier");
                }
                k.setStats(
                        Stats.builder()
                                .kills(u.getUserDTO().getStats().getKills())
                                .deaths(u.getUserDTO().getStats().getDeaths())
                                .wins(u.getUserDTO().getStats().getWins())
                                .losses(u.getUserDTO().getStats().getLosses())
                                .headshots(u.getUserDTO().getStats().getHeadshots())
                                .hits(u.getUserDTO().getStats().getHits())
                                .build());
                k.setGameIDs(u.getGameIDs());
                ((UserRepository) type).save(k);
                return true;
            }else if(type instanceof ChatRepository){
                Type listType = new TypeToken<List<Chat>>(){}.getType();
                List<Chat> l = new Gson().fromJson(json, listType);
                ((ChatRepository) type).saveAll(l);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }catch(com.amazonaws.services.kms.model.NotFoundException e){
            throw new NotFoundException("Nu exista fisierul in baza noastra de date.");
        }
        catch (com.amazonaws.SdkClientException e) {
            throw new NonExistentException("Eroare AWS. Verifica conexiunea la internet." + e.getMessage());
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
        RandomNameGenerator r = RandomNameGenerator.getInstance();
        String fileName = r.generateName();
        File temp = File.createTempFile("temp", ".json");
        try (FileOutputStream fos = new FileOutputStream(temp)) {
            fos.write(userJson.toString().getBytes());
        }
        FileInputStream input = new FileInputStream(temp);
        MultipartFile multipartFile =
                new MockMultipartFile(
                        "fileItem", temp.getName(), "application/json", IOUtils.toByteArray(input));
        s3.uploadFile(fileName + ".json", multipartFile);
        temp.delete();
        return fileName;
    }
}
