package com.xuannie.devatlas.user.common.command;

import com.xuannie.devatlas.common.command.Command;

public record DeleteUserCommand(
        Long userId
) implements Command {}
