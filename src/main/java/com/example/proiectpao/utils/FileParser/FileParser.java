package com.example.proiectpao.utils.FileParser;

import com.example.proiectpao.service.S3Service.S3Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/*
 * Clasa abstracta FileParser este folosita pentru a citi si scrie fisierele cu diferite extensii
 * Design pattern folosit: Strategy
 */

public abstract class FileParser {

    /**
     * Metoda abstracta read este folosita pentru a citi un fisier
     * @param user - Fie userul (folosit pt JSON), fie lista de useri, folosita pt a extrage rezultatele meciului final
     * @param file - Fisierul care trebuie citit
     * @param s3 - S3
     * @return - true daca fisierul a fost citit cu succes, false altfel
     */
    public abstract boolean read(Object user, MultipartFile file, S3Service s3,Object type) throws IOException;

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
    private static final Map<Class<? extends FileParser>, FileParser> instances = new HashMap<>();

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
                            }
                            throw new IllegalArgumentException("Nu pot instantia " + cls.getName());
                        }));
    }
}
