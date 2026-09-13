package com.xuannie.devatlas.user.adapter;

import com.xuannie.devatlas.user.api.request.CreateUserRequest;
import com.xuannie.devatlas.user.api.request.LoginUserRequest;
import com.xuannie.devatlas.user.api.request.UpdateUserRequest;
import com.xuannie.devatlas.user.api.response.AuthResponse;
import com.xuannie.devatlas.user.application.AuthService;
import com.xuannie.devatlas.user.common.command.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody CreateUserRequest request) {
        CreateUserCommand command = UserCommandBuilder.from(request);
        return this.authService.register(command);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginUserRequest request) {
        LoginUserCommand command = UserCommandBuilder.from(request);
        return this.authService.login(command);
    }

    // AuthPrincipal needed so that only user can modify their own account
    @PostMapping("/update")
    public AuthResponse update(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody UpdateUserRequest request
    ) {
        UpdateUserCommand command = UserCommandBuilder.from(userId, request);
        return this.authService.update(userId, command);
    }

    @DeleteMapping("/delete")
    public AuthResponse delete(
        @AuthenticationPrincipal Long userId
    ) {
        DeleteUserCommand command = UserCommandBuilder.from(userId);
        return this.authService.delete(command);
    }
}
