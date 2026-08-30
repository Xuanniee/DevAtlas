package com.xuannie.devatlas.page.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * UpdatePageRequest allows you to update 1 or all the fields at once
 *
 * For fields that should not be updated, I should leave them as null so they wont
 * be updated
 */
@Getter
public class UpdatePageRequest {
    @Size(max = 256, message = "New page name cannot be more than 256 characters.")
    @NotBlank(message = "New page name cannot be empty.")
    private String name;

    // For changing users, which is not always the case, so can be null
    private Long ownerId;

    // Can be null or empty
    private String content;

    public UpdatePageRequest(String name, Long ownerId, String content) {
        this.name = name;
        this.ownerId = ownerId;
        this.content = content;
    }
}
