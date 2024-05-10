package com.example.proiectpao.utils.FileParser;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.model.S3Object;
import com.example.proiectpao.exceptions.NonExistentException;
import com.example.proiectpao.service.S3Service.S3Service;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CsvFileParser extends FileParser{

    @Override
    public boolean read(Object user, MultipartFile file, S3Service s3, Object type) throws IOException {
        try{
            InputStream is = file.getInputStream();
            S3Object s3obj = s3.getFile(file.getOriginalFilename());
            String csv = IOUtils.toString(is, StandardCharsets.UTF_8);
            String S3csv = IOUtils.toString(s3obj.getObjectContent(), StandardCharsets.UTF_8);
            if (!csv.equals(S3csv)) {
                throw new NonExistentException("Continutul back-up-ului a fost modificat!");
            }
        }catch(com.amazonaws.services.kms.model.NotFoundException e){
            throw new NotFoundException("Nu exista fisierul in baza noastra de date.");
        }
        catch (com.amazonaws.SdkClientException e) {
            throw new NonExistentException("Eroare AWS. Verifica conexiunea la internet. " + e.getMessage());
        }
        return false;
    }

    @Override
    public String write(Object data, S3Service s3) throws IOException {
        return null;
    }
}
