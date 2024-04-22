package com.example.proiectpao.utils.FileParser;

import com.example.proiectpao.service.S3Service.S3Service;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/*
 * Clasa abstracta FileParser este folosita pentru a citi si scrie fisierele cu diferite extensii
 * Design pattern folosit: Strategy
 */
public abstract class FileParser {

    /*
        * Metoda abstracta read este folosita pentru a citi un fisier
        * @param user - Fie userul (folosit pt JSON), fie lista de useri, folosita pt a extrage rezultatele meciului final
        * @param file - Fisierul care trebuie citit
        * @param s3 - S3
        * @return - true daca fisierul a fost citit cu succes, false altfel
     */
    public abstract boolean read(Object user, MultipartFile file, S3Service s3);
    /*
        * Metoda abstracta write este folosita pentru a scrie un fisier
        * @param data - datele care trebuie scrise
        * @param s3 - S3
        * @return - numele fisierului scris
     */

    public abstract String write(Object data, S3Service s3) throws IOException;
}
