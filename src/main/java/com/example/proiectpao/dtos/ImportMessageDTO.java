package com.example.proiectpao.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class ImportMessageDTO {
    private String requester;
    private String sender;
    private String receiver;
    private MultipartFile file;
}
