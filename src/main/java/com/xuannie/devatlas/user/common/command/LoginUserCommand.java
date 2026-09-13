package com.xuannie.devatlas.user.common.command;

import com.xuannie.devatlas.common.command.Command;

public record LoginUserCommand(
        String email,
        String password
) implements Command {}
