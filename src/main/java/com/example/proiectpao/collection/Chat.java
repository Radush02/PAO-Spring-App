package com.example.proiectpao.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@NoArgsConstructor
public class Chat {
    @Id private String chatId;
    private Date date;
    private String message;
    private String senderName;
    private String receiverName;
}
