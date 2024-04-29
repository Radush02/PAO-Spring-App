package com.example.proiectpao.utils.FileParser;

import com.amazonaws.services.s3.model.S3Object;
import com.example.proiectpao.collection.User;
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
            /*
             * s3.getfile()
             * Arunca com.amazonaws.SdkClientException daca nu gaseste fisierul
             * Descoperit prin trial and error :)
             */
            User k = (User) user;
            InputStream is = file.getInputStream();
            S3Object s3obj = s3.getFile(file.getOriginalFilename());
            String json = IOUtils.toString(is, StandardCharsets.UTF_8);
            String s3Json = IOUtils.toString(s3obj.getObjectContent(), StandardCharsets.UTF_8);
            if (!json.equals(s3Json)) {
                //                System.out.println(json);
                //                System.out.println(s3Json);
                //                System.out.println("Continutul back-up-ului a fost modificat!");
                throw new NonExistentException("Continutul back-up-ului a fost modificat!");
            }
            User u = new Gson().fromJson(json, User.class);
            if (!Objects.equals(u.getName(), k.getName())) {
                throw new NonExistentException("Numele userului nu corespunde cu cel din fisier");
            }
            k.setStats(u.getStats());
            k.setGameIDs(u.getGameIDs());
            k.setName(u.getName());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (com.amazonaws.SdkClientException e) {
            throw new NonExistentException("Fisierul trimis nu exista in baza noastra de date");
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
