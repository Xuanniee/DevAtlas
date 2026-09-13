package com.xuannie.devatlas.workspace.application;

import com.xuannie.devatlas.workspace.api.request.CreateWorkspaceRequest;
import com.xuannie.devatlas.workspace.api.response.WorkspaceResponse;
import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.workspace.common.command.CreateWorkspaceCommand;
import com.xuannie.devatlas.workspace.domain.entity.Workspace;
import java.util.List;

public interface WorkspaceService {

    List<WorkspaceResponse> listAllWorkspaces(Long userId);

    WorkspaceResponse getWorkspaceById(Long userId, Long workspaceId);

    WorkspaceResponse createWorkspace(Long userId, CreateWorkspaceCommand command);
}
