package com.xuannie.devatlas.workspace.common.exceptions;

import com.xuannie.devatlas.common.error.NotFoundException;

public class WorkspaceNotFoundException extends NotFoundException {
    public WorkspaceNotFoundException(Long workspaceId) {
        super("Workspace not found with id: " + workspaceId);
    }
}
