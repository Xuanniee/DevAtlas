package com.xuannie.devatlas.workspace_members.common.commands;

import com.xuannie.devatlas.workspace_members.api.request.ViewWorkspaceMemberRequest;

public class WorkspaceMemberQueryBuilder {
    private WorkspaceMemberQueryBuilder() {}

    public static GetWorkspaceMemberQuery from(Long userId, Long workspaceId, ViewWorkspaceMemberRequest request) {
        return new GetWorkspaceMemberQuery(userId, request.getTargetUserId(), workspaceId);
    }
}
