package com.example.proiectpao.utils.FileParser;

import com.amazonaws.services.s3.model.S3Object;
import com.example.proiectpao.service.S3Service.S3Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.example.proiectpao.utils.RandomGenerator.RandomNameGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/*
 * Clasa abstracta FileParser este folosita pentru a citi si scrie fisierele cu diferite extensii
 * Design pattern folosit: Strategy
 */

public abstract class FileParser {
    private static final Map<Class<? extends FileParser>, FileParser> instances = new HashMap<>();
    /**
     * Metoda abstracta read este folosita pentru a citi un fisier
     * @param user - Fie userul (folosit pt JSON), fie lista de useri, folosita pt a extrage rezultatele meciului final
     * @param file - Fisierul care trebuie citit
     * @param s3 - S3
     * @return - true daca fisierul a fost citit cu succes, false altfel
     */
    public abstract boolean read(Object user, MultipartFile file, S3Service s3, Object type)
            throws IOException;

    /**
     * Metoda abstracta write este folosita pentru a scrie un fisier
     * @param data - datele care trebuie scrise
     * @param s3 - S3
     * @return - numele fisierului scris
     */
    public abstract String write(Object data, S3Service s3) throws IOException;

    /**
     * Metoda getInstance este folosita pentru a instantia un obiect de tip FileParser in functie de tipul de FileParser primit ca parametru
     * @param cls - tipul de FileParser (JsonFileParser sau ScoreboardFileParser)
     * @return - obiectul de tip FileParser
     */
    public static synchronized <T extends FileParser> T getInstance(Class<T> cls) {
        return cls.cast(
                instances.computeIfAbsent(
                        cls,
                        key -> {
                            if (cls == JsonFileParser.class) {
                                return new JsonFileParser();
                            } else if (cls == ScoreboardFileParser.class) {
                                return new ScoreboardFileParser();
                            }else if (cls == CsvFileParser.class) {
                                return new CsvFileParser();
                            }
                            throw new IllegalArgumentException("Nu pot instantia " + cls.getName());
                        }));
    }

    /**
     * Metoda getFileContent este folosita pentru a citi continutul unui fisier
     * @param file - fisierul care trebuie citit
     * @return - continutul fisierului
     * @throws IOException - exceptie in cazul in care fisierul nu poate fi citit
     */
    protected String getFileContent(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Metoda getS3FileContent este folosita pentru a citi continutul unui fisier de pe S3
     * @param fileName - numele fisierului
     * @param s3 - S3
     * @return - continutul fisierului
     * @throws IOException - exceptie in cazul in care fisierul nu poate fi citit
     */
    protected String getS3FileContent(String fileName, S3Service s3) throws IOException {
        S3Object s3Object = s3.getFile(fileName);
        try (InputStream is = s3Object.getObjectContent()) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    /**
     * Metoda generateFileName este folosita pentru a genera random un nume de fisier
     * @return - numele fisierului generat
     */
    protected String generateFileName() {
        RandomNameGenerator generator = RandomNameGenerator.getInstance();
        return generator.generateName();
    }

    /**
     * Metoda createTempFile creeaza un fisier ce va urma sa fie salvat
     * @param obj - datele ce trebuie scrise
     * @param ext - extensia fisierului
     * @return
     * @throws IOException
     */
    protected File createTempFile(Object obj, String ext) throws IOException {
        File tempFile = File.createTempFile("temp", ext);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(obj.toString().getBytes(StandardCharsets.UTF_8));
        }
        return tempFile;
    }

    /**
     * Metoda createMultipartFile creeaza un obiect de tip MultipartFile
     * @param tempFile - fisierul ce trebuie transformat in MultipartFile
     * @param input - input stream-ul fisierului
     * @return - obiectul de tip MultipartFile
     * @throws IOException - exceptie in cazul in care fisierul nu poate fi citit
     */
    protected MultipartFile createMultipartFile(File tempFile, FileInputStream input) throws IOException {
        return new MockMultipartFile(
                "fileItem", tempFile.getName(), "application/json", IOUtils.toByteArray(input));
    }
}
