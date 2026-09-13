package com.xuannie.devatlas.user.application;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface JwtService {
    String issueToken(Long userId);

    Long parseUserId(String token);
}
