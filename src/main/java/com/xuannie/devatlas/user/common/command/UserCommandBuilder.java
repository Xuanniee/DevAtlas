package com.xuannie.devatlas.user.common.command;

import com.xuannie.devatlas.user.api.request.CreateUserRequest;
import com.xuannie.devatlas.user.api.request.LoginUserRequest;
import com.xuannie.devatlas.user.api.request.UpdateUserRequest;

public final class UserCommandBuilder {
    private UserCommandBuilder() {}

    public static CreateUserCommand from(CreateUserRequest request) {
        return new CreateUserCommand(request.getName(), request.getEmail(), request.getPassword());
    }

    public static LoginUserCommand from(LoginUserRequest request) {
        return new LoginUserCommand(request.getEmail(), request.getPassword());
    }

    public static UpdateUserCommand from(Long userId, UpdateUserRequest request) {
        return new UpdateUserCommand(userId, request.getNewName(), request.getNewEmail(), request.getNewPassword());
    }

    public static DeleteUserCommand from(Long userId) {
        return new DeleteUserCommand(userId);
    }
}
