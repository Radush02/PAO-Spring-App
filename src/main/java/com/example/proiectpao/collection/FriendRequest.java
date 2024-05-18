package com.example.proiectpao.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clasa FriendRequest reprezinta o cerere de prietenie trimisa de un utilizator catre altul.<br>
 * O cerere de prietenie contine un id, numele celui care a trimis cererea si numele celui care a primit-o.
 */
@Data
@Document(collection = "friendRequests")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class FriendRequest {
    @Id private String id;
    private String sender;
    private String receiver;
}
