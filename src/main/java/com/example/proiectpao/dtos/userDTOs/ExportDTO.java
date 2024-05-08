package com.example.proiectpao.dtos.userDTOs;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ExportDTO {
    private UserDTO userDTO;
    private List<String> gameIDs;
}
