package com.xuannie.devatlas.page.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class Page {
    private Long id;
    private String name;
    private Long workspaceId;
    private Long parentId;
    private Long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime archivedAt;

    public Page() {
    }

    public Page(
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
