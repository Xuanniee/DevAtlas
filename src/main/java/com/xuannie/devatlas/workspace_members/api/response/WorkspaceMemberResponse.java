package com.xuannie.devatlas.workspace_members.api.response;

import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkspaceMemberResponse {
    private Long workspaceId;
    private Long userId;
    private WorkspaceMemberRole role;
    private LocalDateTime joinedAt;

    public WorkspaceMemberResponse(Long workspaceId, Long userId, WorkspaceMemberRole role, LocalDateTime joinedAt) {
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}
