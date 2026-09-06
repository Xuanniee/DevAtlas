package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.common.command.Command;

public record CreatePageRevisionCommand(
        Long ownerId,
        Long pageId,
        String name,
        String content,
        String note,
        Long editedBy
) implements Command {}