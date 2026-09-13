package com.xuannie.devatlas.workspace_members.common.commands;

import com.xuannie.devatlas.common.command.Command;
import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;

public record UpdateWorkspaceMemberCommand(
        Long executingUserId,
        Long targetUserId,
        Long workspaceId,
        WorkspaceMemberRole newRole
) implements Command {}
