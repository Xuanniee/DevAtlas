package com.xuannie.devatlas.workspace.domain.entity;

import com.xuannie.devatlas.workspace.common.enums.WorkspaceCategory;
import com.xuannie.devatlas.workspace.common.enums.WorkspacePermissions;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceStatus;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceVisibility;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Workspace {
    private Long id;
    // Unique slug for the workspace, used in URLs
    private String slug;
    private String name;
    private String description;
    private WorkspaceStatus status;
    // Visibility of the workspace, e.g., PUBLIC, PRIVATE, TEAM_ONLY
    private WorkspaceVisibility visibility;
    // Category of the workspace, e.g., PERSONAL, TEAM, PUBLIC
    private WorkspaceCategory category;
    // ID of the root page of workspace (ForeignKey)
    private Long homePageId;
    private Long ownerId;
    // Permissions of the workspace, e.g., VIEWER, EDITOR, ADMIN
    private WorkspacePermissions permissions;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime archivedAt;

    public Workspace() {}

    public Workspace(Long id, String slug, String name, String description, WorkspaceStatus status, WorkspaceVisibility visibility, WorkspaceCategory category, Long homePageId, Long ownerId, WorkspacePermissions permissions, LocalDateTime createdAt, LocalDateTime modifiedAt, LocalDateTime archivedAt) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.status = status;
        this.visibility = visibility;
        this.category = category;
        this.permissions = permissions;
        this.homePageId = homePageId;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.archivedAt = archivedAt;
    }
}
