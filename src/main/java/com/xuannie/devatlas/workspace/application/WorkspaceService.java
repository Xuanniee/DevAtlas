package com.xuannie.devatlas.workspace.application;

import com.xuannie.devatlas.workspace.api.request.CreateWorkspaceRequest;
import com.xuannie.devatlas.workspace.api.response.WorkspaceResponse;
import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.workspace.common.command.CreateWorkspaceCommand;
import com.xuannie.devatlas.workspace.domain.entity.Workspace;
import java.util.List;

public interface WorkspaceService {

    List<Workspace> listAllWorkspaces(Long ownerId);

    WorkspaceResponse getWorkspaceById(Long ownerId, Long workspaceId);

    WorkspaceResponse createWorkspace(Long ownerId, CreateWorkspaceCommand command);


}
