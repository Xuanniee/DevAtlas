package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.common.command.Command;

public record MoveOrRetrievePageCommand(
    Long pageId
) implements Command {}
