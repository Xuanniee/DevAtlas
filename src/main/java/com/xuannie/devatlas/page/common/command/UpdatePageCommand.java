package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.common.command.Command;

// TODO what is a sealed interface for
public record UpdatePageCommand(
        Long pageId,
        String name,
        String content,
        Long ownerId,
        String updateNote
) implements Command {}