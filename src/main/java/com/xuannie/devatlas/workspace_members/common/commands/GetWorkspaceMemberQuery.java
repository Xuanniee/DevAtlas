package com.xuannie.devatlas.workspace_members.common.commands;

import com.xuannie.devatlas.common.command.Query;

public record GetWorkspaceMemberQuery(
        Long executingUserId,
        Long targetUserId,
        Long workspaceId
) implements Query {}
