package com.xuannie.devatlas.workspace.api.request;

import lombok.Getter;

@Getter
public class DeleteWorkspaceMemberRequest {
    // Represents the User whose WorkspaceMembership is getting stripped
    private Long targetUserId;
}
