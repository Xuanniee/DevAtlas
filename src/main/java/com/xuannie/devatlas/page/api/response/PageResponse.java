package com.xuannie.devatlas.page.api.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PageResponse {
    private Long id;
    private String name;
    private Long workspaceId;
    private Long parentId;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime archivedAt;

    public PageResponse(
            Long id,
            String name,
            Long workspaceId,
            Long parentId,
            Long ownerId,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            LocalDateTime archivedAt
    ) {
        this.id = id;
        this.name = name;
        this.workspaceId = workspaceId;
        this.parentId = parentId;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.archivedAt = archivedAt;
    }
}
