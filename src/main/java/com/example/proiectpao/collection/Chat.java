package com.example.proiectpao.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.sql.Timestamp;
import java.util.Date;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "chats")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class Chat {
    @Id private String chatId;
    private Timestamp date;
    private String message;
    private String senderName;
    private String receiverName;

    public Chat() {
        Date d = new Timestamp(new Date().getTime());
    }
}
