package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.common.command.Command;

public record CreatePageCommand(
    String name,
    Long ownerId,
    Long parentId,
    String content
) implements Command {}
