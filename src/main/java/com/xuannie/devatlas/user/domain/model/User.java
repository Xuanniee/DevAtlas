package com.xuannie.devatlas.user.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class User {
    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
