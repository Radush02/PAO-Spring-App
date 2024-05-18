package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Actions;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "adminLog")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class AdminLog {
    public String admin;
    public String user;
    public Actions action;
    public Date date;
    public Date expiryDate;

    public AdminLog(String admin, Actions action) {
        this.admin = admin;
        this.user = null;
        this.action = action;
        this.date = new Date();
        this.expiryDate = null;
    }

    public AdminLog(String admin, String user, Actions action) {
        this.admin = admin;
        this.user = user;
        this.action = action;
        this.date = new Date();
        this.expiryDate = null;
    }

    public AdminLog(String admin, String user, Actions action, Date expiryDate) {
        this.admin = admin;
        this.user = user;
        this.action = action;
        this.date = new Date();
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "AdminLog{"
                + "admin='"
                + admin
                + '\''
                + ", user='"
                + user
                + '\''
                + ", action="
                + action
                + ", date="
                + date
                + '}';
    }
}
