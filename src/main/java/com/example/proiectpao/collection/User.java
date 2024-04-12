package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
public class User {
    @Id private String userId;
    private Role role;
    private String username;
    private String seed;
    private String hash;
    private String email;
    private String name;
    private Stats stats;

    public User() {
        this.role = Role.User;
    }
}
