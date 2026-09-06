package com.xuannie.devatlas.user.api.response;

import lombok.Setter;

@Setter
public class UserResponse {
    private String name;
    private String email;

    public UserResponse(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
