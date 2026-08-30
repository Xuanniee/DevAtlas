package com.xuannie.devatlas.workspace.api.response;

import com.xuannie.devatlas.workspace.common.enums.WorkspaceCategory;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceStatus;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceVisibility;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WorkspaceResponse {
    private final Long id;
    private final String slug;
    private final String name;
    private final String description;
    private final WorkspaceStatus status;
    private final WorkspaceVisibility visibility;
    private final WorkspaceCategory category;
    private final Long ownerId;
    private final Long homePageId;
    private final LocalDateTime createdAt;

    public WorkspaceResponse(Long id, String slug, String name, String description, WorkspaceStatus status,
                             WorkspaceVisibility visibility, WorkspaceCategory category,
                             Long ownerId, Long homePageId, LocalDateTime createdAt) {
        this.id = id;
        this.slug = slug;
        this.name = name;
        this.description = description;
        this.status = status;
        this.visibility = visibility;
        this.category = category;
        this.ownerId = ownerId;
        this.homePageId = homePageId;
        this.createdAt = createdAt;
    }
}