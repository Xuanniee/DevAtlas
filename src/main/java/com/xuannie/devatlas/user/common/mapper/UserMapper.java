package com.xuannie.devatlas.user.common.mapper;

import com.xuannie.devatlas.user.api.response.AuthResponse;
import com.xuannie.devatlas.user.domain.model.User;

public class UserMapper {
    public static AuthResponse toResponse(User user) {
        return AuthResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static AuthResponse toResponse(String jwtToken) {
        return AuthResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }
}
