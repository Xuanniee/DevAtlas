package com.xuannie.devatlas.workspace_members.common.commands;

public class WorkspaceMemberQueryBuilder {
    private WorkspaceMemberQueryBuilder() {}

    public static GetWorkspaceMemberQuery from(Long userId, Long workspaceId, Long targetUserId) {
        return new GetWorkspaceMemberQuery(userId, targetUserId, workspaceId);
    }
}
