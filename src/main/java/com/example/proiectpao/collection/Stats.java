package com.example.proiectpao.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class Stats {
    private int wins;
    private int losses;
    private int hits;
    private int headshots;
    private int kills;
    private int deaths;
}
