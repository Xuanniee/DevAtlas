package com.xuannie.devatlas.workspace_members.api.request;

import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;
import lombok.Getter;

// Invites a Member to the Workspace
@Getter
public class CreateWorkspaceMemberRequest {
    private Long targetUserId;
    private WorkspaceMemberRole role;

    public CreateWorkspaceMemberRequest(Long targetUserId, WorkspaceMemberRole role) {
        this.targetUserId = targetUserId;
        this.role = role;
    }
}
