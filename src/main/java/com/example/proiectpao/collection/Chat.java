package com.example.proiectpao.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.sql.Timestamp;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "chats")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class Chat {
    @Id private String chatId;
    private Timestamp date;
    private String message;
    private String senderId;
    private String receiverId;

    public Chat(String chatId, Timestamp date, String message, String senderId, String receiverId) {
        this.chatId = chatId;
        this.date = date;
        this.message = message;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Chat() {
        Date d = new Timestamp(new Date().getTime());
    }
}
