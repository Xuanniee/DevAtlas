package com.xuannie.devatlas.user.application;

import com.xuannie.devatlas.user.api.response.AuthResponse;
import com.xuannie.devatlas.user.common.command.CreateUserCommand;
import com.xuannie.devatlas.user.common.command.LoginUserCommand;

public interface AuthService {
    AuthResponse register(CreateUserCommand command);

    AuthResponse login(LoginUserCommand command);
}
