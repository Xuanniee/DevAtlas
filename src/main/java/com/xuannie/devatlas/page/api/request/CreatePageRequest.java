package com.xuannie.devatlas.page.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * This creates a non-root page in any workspace for the user.
 */
@Getter
public class CreatePageRequest {
    // Name is Optional since it starts off as Untitled
    @Size(max = 256, message = "Name cannot be more than 256 characters.")
    private String name;

    @NotNull(message = "ownerId cannot be null as page must belong to a user")
    private Long ownerId;

    // Cannot be NULL since root pages are created elsewhere
    @NotNull(message = "parentId cannot be NULL since root pages are created at workspace level")
    private Long parentId;

    @NotNull(message = "Page body content can only be empty, not null")
    private String content;

    // WorkspaceId is not provided by the Client since it should be derived from the
    // workspace the user is in
    public CreatePageRequest(String name, String content, Long workspaceId, Long ownerId, Long parentId) {
        this.name = name;
        this.ownerId = ownerId;
        this.parentId = parentId;
        this.content = content;
    }
}
