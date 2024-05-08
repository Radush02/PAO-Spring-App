package com.example.proiectpao.utils.FileParser;

import com.amazonaws.services.s3.model.S3Object;
import com.example.proiectpao.collection.Stats;
import com.example.proiectpao.collection.User;
import com.example.proiectpao.dtos.userDTOs.ExportDTO;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.service.S3Service.S3Service;
import com.example.proiectpao.utils.RandomGenerator.RandomNameGenerator;
import com.google.gson.Gson;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class JsonFileParser extends FileParser {

    @Override
    public boolean read(Object user, MultipartFile file, S3Service s3) {
        try {
            User k = (User) user;
            InputStream is = file.getInputStream();
            S3Object s3obj = s3.getFile(file.getOriginalFilename());
            String json = IOUtils.toString(is, StandardCharsets.UTF_8);
            String s3Json = IOUtils.toString(s3obj.getObjectContent(), StandardCharsets.UTF_8);
            if (!json.equals(s3Json)) {
                System.out.println(json);
                System.out.println(s3Json);
                //                System.out.println("Continutul back-up-ului a fost modificat!");
                throw new NonExistentException("Continutul back-up-ului a fost modificat!");
            }
            ExportDTO u = new Gson().fromJson(json, ExportDTO.class);
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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (com.amazonaws.SdkClientException e) {
            System.err.println(e.getMessage());
            throw new NonExistentException("Eroare AWS");
        }
        return true;
    }

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
