package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Penalties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document(collection = "punishLogs")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class Punish {
    @Id private String id;
    private String userID;
    private String adminID;
    private String reason;
    private Date date;
    private Date expiryDate;

    private Penalties sanction;

    public static class PunishBuilder {
        private String userID;
        private String adminID;
        private String reason;
        private Date date = new Date();
        private Date expiryDate;
        private Penalties sanction;
    }
}
