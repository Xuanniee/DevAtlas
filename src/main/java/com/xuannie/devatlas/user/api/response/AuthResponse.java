package com.xuannie.devatlas.user.api.response;

import com.xuannie.devatlas.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String jwtToken;
    private String name;
    private String email;

    public AuthResponse(String jwtToken, String name, String email) {
        this.name = name;
        this.email = email;
        this.jwtToken = jwtToken;
    }


}
