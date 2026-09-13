package com.xuannie.devatlas.workspace_members.common.mapper;

import com.xuannie.devatlas.workspace_members.api.response.WorkspaceMemberResponse;
import com.xuannie.devatlas.workspace_members.common.commands.CreateWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;

public class WorkspaceMemberMapper {
    private WorkspaceMemberMapper() {}

    public static WorkspaceMember toExecutingEntity(CreateWorkspaceMemberCommand command) {
        return WorkspaceMember.builder()
                .workspaceId(command.workspaceId())
                .userId(command.executingUserId())
                .build();
    }

    public static WorkspaceMember toTargetEntity(CreateWorkspaceMemberCommand command) {
        return WorkspaceMember.builder()
                .workspaceId(command.workspaceId())
                .userId(command.targetUserId())
                .role(command.role())
                .build();
    }

    public static WorkspaceMemberResponse toResponse(WorkspaceMember workspaceMember) {
        return new WorkspaceMemberResponse(
                workspaceMember.getWorkspaceId(),
                workspaceMember.getUserId(),
                workspaceMember.getRole(),
                workspaceMember.getJoinedAt()
        );
    }
}
