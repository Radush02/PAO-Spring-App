package com.example.proiectpao.dtos;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PunishDTO {
    private String username;
    private String admin;
    private String reason;
    @Setter private Date expiryDate;
}
