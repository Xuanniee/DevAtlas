package com.xuannie.devatlas.workspace_members.common.exception;

import com.xuannie.devatlas.common.error.ConflictException;

public class ExistingWorkspaceMemberException extends ConflictException {
    public ExistingWorkspaceMemberException(Long targetUserId) {
        super("User " + targetUserId + " alreadys has an existing WorkspaceMemberRole.");
    }
}
