package com.xuannie.devatlas.user.common.command;

import com.xuannie.devatlas.common.command.Command;

public record UpdateUserCommand(
        Long userId,
        String newName,
        String newEmail,
        String newPassword
) implements Command {}
