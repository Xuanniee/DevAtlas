package com.xuannie.devatlas.workspace.common.command;

import com.xuannie.devatlas.workspace.api.request.CreateWorkspaceRequest;

public final class WorkspaceCommandBuilder {
    private WorkspaceCommandBuilder() {}

    public static CreateWorkspaceCommand from(CreateWorkspaceRequest request) {
        return new CreateWorkspaceCommand(
                request.getName(),
                request.getDescription(),
                request.getVisibility(),
                request.getCategory()
        );
    }
}
