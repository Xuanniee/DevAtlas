package com.xuannie.devatlas.page.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreatePageRevisionRequest {
    @NotNull(message = "Page ID must be provided when creating a revision for a page.")
    private Long pageId;

    @NotNull(message = "Page revision name must not be null.")
    private String name;

    private String content;

    private String note;

    @NotNull(message = "User ID of the user that revised this apge must be provided.")
    private Long editedBy;

    public CreatePageRevisionRequest(Long pageId, String name, String content, String note, Long editedBy) {
        this.pageId = pageId;
        this.name = name;
        this.content = content;
        this.note = note;
        this.editedBy = editedBy;
    }
}
