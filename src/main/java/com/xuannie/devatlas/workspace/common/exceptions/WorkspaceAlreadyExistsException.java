package com.xuannie.devatlas.workspace.common.exceptions;

import com.xuannie.devatlas.common.error.ConflictException;

public class WorkspaceAlreadyExistsException extends ConflictException {
    public WorkspaceAlreadyExistsException(String workspaceName) {
        super("Workspace already existing with name: " + workspaceName);
    }
}
