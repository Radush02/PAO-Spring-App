package com.example.proiectpao.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Clasa Chat reprezinta un mesaj trimis de un utilizator catre altul.<br>
 * Un chat contine un id, data la care a fost trimis, mesajul in sine, numele celui care l-a trimis si numele celui care l-a primit.
 * @author Radu
 */
@Data
@Builder
@Document(collection = "chats")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    @Id private String chatId;
    private Date date;
    private String message;
    private String senderName;
    private String receiverName;
}
