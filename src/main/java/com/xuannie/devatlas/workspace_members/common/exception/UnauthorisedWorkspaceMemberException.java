package com.xuannie.devatlas.workspace_members.common.exception;

import com.xuannie.devatlas.common.error.ForbiddenException;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;

public class UnauthorisedWorkspaceMemberException extends ForbiddenException {
    public UnauthorisedWorkspaceMemberException(Long executingUserId, String operation) {
        super("User " + executingUserId + " does not have sufficient authorisation to perform " + operation);
    }
}
