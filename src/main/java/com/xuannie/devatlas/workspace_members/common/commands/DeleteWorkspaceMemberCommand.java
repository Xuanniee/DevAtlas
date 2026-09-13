package com.xuannie.devatlas.workspace_members.common.commands;

import com.xuannie.devatlas.common.command.Command;

public record DeleteWorkspaceMemberCommand(
        Long executingUserId,
        Long targetUserId,
        Long workspaceId
) implements Command {}
