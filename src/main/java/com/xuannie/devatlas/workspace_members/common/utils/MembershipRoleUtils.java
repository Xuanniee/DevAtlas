package com.xuannie.devatlas.workspace_members.common.utils;

import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import com.xuannie.devatlas.workspace_members.common.exception.UnauthorisedWorkspaceMemberException;
import com.xuannie.devatlas.workspace_members.common.exception.WorkspaceMemberNotFoundException;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;
import com.xuannie.devatlas.workspace_members.domain.repository.WorkspaceMemberRepository;

public final class MembershipRoleUtils {
    private MembershipRoleUtils() {}

    public static void validateOperationByRole(
            WorkspaceMemberRepository workspaceMemberRepository,
            Long userId,
            Long workspaceId,
            WorkspaceMemberRole role,
            String operation
    ) {
        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceId(userId, workspaceId)
                .orElseThrow(() -> new WorkspaceMemberNotFoundException(userId, workspaceId));
        if (!workspaceMember.getRole().isAtLeast(role)) {
            throw new UnauthorisedWorkspaceMemberException(userId, operation);
        }
    }
}
