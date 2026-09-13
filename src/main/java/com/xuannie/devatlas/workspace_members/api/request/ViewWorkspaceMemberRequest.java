package com.xuannie.devatlas.workspace_members.api.request;

import lombok.Getter;

@Getter
public class ViewWorkspaceMemberRequest {
    private Long targetUserId;

    public ViewWorkspaceMemberRequest(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
}
