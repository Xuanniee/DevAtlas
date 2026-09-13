package com.xuannie.devatlas.workspace_members.api.request;

import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;
import lombok.Getter;

@Getter
public class UpdateWorkspaceMemberRequest {
    private Long targetUserId;
    private WorkspaceMemberRole role;

    public UpdateWorkspaceMemberRequest(Long targetUserId, WorkspaceMemberRole role) {
        this.targetUserId = targetUserId;
        this.role = role;
    }
}