package com.example.proiectpao.dtos;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessageDTO {
    private String message;
    private String senderName;
    private Date date;
}
