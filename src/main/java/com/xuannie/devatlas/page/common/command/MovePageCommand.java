package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.common.command.Command;

public record MovePageCommand(
    Long ownerId,
    Long currentPageId,
    Long targetPageId
) implements Command {}
