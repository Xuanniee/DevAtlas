package com.xuannie.devatlas.user.common.command;

import com.xuannie.devatlas.common.command.Command;

public record UpdateUserCommand(
        String name,
        String email,
        String passwordHash
) implements Command {}
