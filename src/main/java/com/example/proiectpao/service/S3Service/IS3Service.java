package com.example.proiectpao.service.S3Service;

import com.amazonaws.services.s3.model.S3Object;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    /**
     * Incarca un fisier in S3
     * @param keyName - numele fisierului
     * @param file - fisierul incarcat
     */
    public void uploadFile(String keyName, MultipartFile file) throws IOException;

    /**
     * Descarca un fisier din S3
     * @param keyName - numele fisierului
     * @return obiectul descarcat
     */
    public S3Object getFile(String keyName);
}
