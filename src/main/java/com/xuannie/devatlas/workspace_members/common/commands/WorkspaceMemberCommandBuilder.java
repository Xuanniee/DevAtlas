package com.xuannie.devatlas.workspace_members.common.commands;

import com.xuannie.devatlas.workspace_members.api.request.CreateWorkspaceMemberRequest;
import com.xuannie.devatlas.workspace_members.api.request.UpdateWorkspaceMemberRequest;

public class WorkspaceMemberCommandBuilder {
    private WorkspaceMemberCommandBuilder() {}

    public static CreateWorkspaceMemberCommand from(Long executingUserId, Long workspaceId, CreateWorkspaceMemberRequest request) {
        return new CreateWorkspaceMemberCommand(executingUserId, request.getTargetUserId(), workspaceId, request.getRole());
    }

    public static UpdateWorkspaceMemberCommand from(Long executingUserId, Long workspaceId, UpdateWorkspaceMemberRequest request) {
        return new UpdateWorkspaceMemberCommand(executingUserId, request.getTargetUserId(), workspaceId, request.getRole());
    }

    public static DeleteWorkspaceMemberCommand from(Long executingUserId, Long workspaceId, Long targetUserId) {
        return new DeleteWorkspaceMemberCommand(executingUserId, workspaceId, targetUserId);
    }
}
