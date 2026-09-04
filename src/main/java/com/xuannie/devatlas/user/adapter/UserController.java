package com.xuannie.devatlas.user.adapter;

import com.xuannie.devatlas.user.api.request.CreateUserRequest;
import com.xuannie.devatlas.user.api.request.LoginUserRequest;
import com.xuannie.devatlas.user.api.response.AuthResponse;
import com.xuannie.devatlas.user.application.AuthService;
import com.xuannie.devatlas.user.common.command.CreateUserCommand;
import com.xuannie.devatlas.user.common.command.LoginUserCommand;
import com.xuannie.devatlas.user.common.command.UserCommandBuilder;
import com.xuannie.devatlas.user.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
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
}
