package com.xuannie.devatlas.user.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

@Configuration
public class SecurityConfig {
    // For encoding password hashes
    @Bean
    public PasswordEncoder passwordEncoder() {
        String defaultId = "argon2";
        Map<String, PasswordEncoder> encoders = Map.of(
            "argon2", new Argon2PasswordEncoder(16, 32, 1, 1 << 14, 2)
        );

        return new DelegatingPasswordEncoder(defaultId, encoders);
    }

    // TODO understand this
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/actuator/health").permitAll()
                        .anyRequest().authenticated()
                );
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) — next piece, Task 12
        return http.build();
    }
}
