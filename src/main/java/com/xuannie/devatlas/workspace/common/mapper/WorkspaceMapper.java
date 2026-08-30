package com.xuannie.devatlas.workspace.common.mapper;

import com.xuannie.devatlas.workspace.api.request.CreateWorkspaceRequest;
import com.xuannie.devatlas.workspace.api.response.WorkspaceResponse;
import com.xuannie.devatlas.workspace.common.enums.WorkspacePermissions;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceStatus;
import com.xuannie.devatlas.workspace.domain.entity.Workspace;


public class WorkspaceMapper {
    // Prevent construction since this is a static utility class
    private WorkspaceMapper() {};

    // Use Builder to create the workspace
    public static Workspace toEntity(
        CreateWorkspaceRequest request,
        Long ownerId,
        String slug
    ) {
        return Workspace.builder()
                .slug(slug)
                .name(request.getName())
                .description(request.getDescription())
                .status(WorkspaceStatus.ACTIVE)
                .visibility(request.getVisibility())
                .category(request.getCategory())
                .ownerId(ownerId)
                .permissions(WorkspacePermissions.ADMIN)
                .build();
    }

    public static WorkspaceResponse toResponse(Workspace workspace) {
        return new WorkspaceResponse(
            workspace.getId(),
            workspace.getSlug(),
            workspace.getName(),
            workspace.getDescription(),
            workspace.getStatus(),
            workspace.getVisibility(),
            workspace.getCategory(),
            workspace.getOwnerId(),
            workspace.getHomePageId(),
            workspace.getCreatedAt()
        );
    }
}
