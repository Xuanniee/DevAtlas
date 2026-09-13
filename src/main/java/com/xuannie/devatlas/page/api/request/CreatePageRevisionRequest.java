package com.xuannie.devatlas.page.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreatePageRevisionRequest {
    @NotNull(message = "Page revision name must not be null.")
    private String name;

    private String content;

    private String note;

    @NotNull(message = "User ID of the user that revised this apge must be provided.")
    private Long editedBy;

    public CreatePageRevisionRequest(String name, String content, String note, Long editedBy) {
        this.name = name;
        this.content = content;
        this.note = note;
        this.editedBy = editedBy;
    }
}
