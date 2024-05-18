package com.example.proiectpao.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clasa Lobby reprezinta un lobby creat de un utilizator.<br>
 * Un lobby contine un id, numele liderului, numele lobby-ului si lista de jucatori din lobby.
 */
@Builder
@Document(collection = "lobbies")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class Lobby {
    private @Id String id;
    private String lobbyLeader;
    private String lobbyName;
    private List<String> players;
}
