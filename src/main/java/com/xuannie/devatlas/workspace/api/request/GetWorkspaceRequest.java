package com.xuannie.devatlas.workspace.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class GetWorkspaceRequest {
    @NotNull(message = "workspaceId cannot be null when retrieving a singular workspace")
    @NotBlank(message = "workspaceId cannot be blank when retrieving a singular workspace")
    private Long workspaceId;

    public GetWorkspaceRequest(Long workspaceId) {
        this.workspaceId = workspaceId;
    }
}
