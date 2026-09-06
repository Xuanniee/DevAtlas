package com.xuannie.devatlas.page.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PageRevision {
    // No constructor exist for Lombok to get us one
    private Long id;
    private Long pageId;
    private int revisionNumber;
    private String name;
    private String content;
    // Summary note about what the revision was about
    private String note;
    private Long ownerId;
    private Long editedBy;
    // Immutable date time for revisions since they're a record
    private LocalDateTime createdAt;
}
