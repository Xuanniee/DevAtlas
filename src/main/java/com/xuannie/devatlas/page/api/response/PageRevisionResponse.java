package com.xuannie.devatlas.page.api.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class PageRevisionResponse {
    private Long pageId;
    private int revisionNumber;
    private String name;
    private String content;
    private String note;
    private Long editedBy;
    private LocalDateTime createdAt;

    // Include fields that we want to return
    public PageRevisionResponse(
            Long pageId,
            int revisionNumber,
            String name,
            String content,
            String note,
            Long editedBy,
            LocalDateTime createdAt
    ) {
        this.pageId = pageId;
        this.revisionNumber = revisionNumber;
        this.name = name;
        this.content = content;
        this.note = note;
        this.editedBy = editedBy;
        this.createdAt = createdAt;
    }
}
