package com.xuannie.devatlas.user.application;

import com.xuannie.devatlas.user.api.response.AuthResponse;
import com.xuannie.devatlas.user.common.command.CreateUserCommand;
import com.xuannie.devatlas.user.common.command.DeleteUserCommand;
import com.xuannie.devatlas.user.common.command.LoginUserCommand;
import com.xuannie.devatlas.user.common.command.UpdateUserCommand;

public interface AuthService {
    AuthResponse register(CreateUserCommand command);

    AuthResponse login(LoginUserCommand command);

    AuthResponse update(Long userId, UpdateUserCommand command);

    AuthResponse delete(DeleteUserCommand command);
}
