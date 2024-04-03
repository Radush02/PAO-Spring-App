package com.example.proiectpao.collection;

import com.example.proiectpao.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class User {
    @Id
    private String userId;
    private Role role;
    private String username;
    private String seed;
    private String hash;
    private String email;
    private String name;
    public User(){
        this.role = Role.User;
    }
    public User(String userId, Role role, String username, String seed, String hash, String email, String name){
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.seed = seed;
        this.hash = hash;
        this.email = email;
        this.name = name;
    }
}
