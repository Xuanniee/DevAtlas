package com.xuannie.devatlas.workspace_members.common.exception;

import com.xuannie.devatlas.common.error.NotFoundException;

public class WorkspaceMemberNotFoundException extends NotFoundException {
    public WorkspaceMemberNotFoundException(Long userId, Long workspaceId) {
        super("WorkspaceMember with userId " + userId + " and workspaceId " + workspaceId + "is not found.");
    }
}
