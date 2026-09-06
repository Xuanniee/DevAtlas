package com.xuannie.devatlas.user.application;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService{
    private final SecretKey jwt_key;
    private final long expirationMillis;

    /**
     * @Value("${jwt.secret}") doesn't read an OS environment variable directly.
     * It asks Spring's Environment abstraction for a property named jwt.secret,
     * and Spring checks several sources for that property, in priority order
     * (command-line args, then OS environment variables, then application.yml/
     * .properties, then defaults).
     * @param secret
     * @param expirationMillis
     */
    public JwtServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMillis
    ) {
        this.jwt_key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMillis = expirationMillis;
    }

    @Override
    public String issueToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(jwt_key)
                .compact();
    }

    @Override
    public Long parseUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwt_key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Get User ID from Token
        return Long.parseLong(claims.getSubject());
    }
}
