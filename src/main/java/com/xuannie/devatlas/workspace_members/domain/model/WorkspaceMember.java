package com.xuannie.devatlas.workspace_members.domain.model;

import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class WorkspaceMember {
    private Long id;
    private Long workspaceId;
    private Long userId;
    private WorkspaceMemberRole role;
    private LocalDateTime joinedAt;

    public WorkspaceMember(Long id, Long workspaceId, Long userId, WorkspaceMemberRole role, LocalDateTime joinedAt) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}
