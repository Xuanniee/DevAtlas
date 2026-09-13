package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.common.command.Command;

public record MovePageCommand(
    Long userId,
    Long workspaceId,
    Long currentPageId,
    Long targetPageId
) implements Command {}
