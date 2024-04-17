package com.example.proiectpao.service.S3Service;

import com.amazonaws.services.s3.model.S3Object;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface IS3Service {

    public void uploadFile(String keyName, MultipartFile file) throws IOException;

    public S3Object getFile(String keyName);
}
