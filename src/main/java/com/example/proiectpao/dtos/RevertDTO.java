package com.example.proiectpao.dtos;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RevertDTO {
    public String admin;
    public String user;
    public Date expiryDate;
}
