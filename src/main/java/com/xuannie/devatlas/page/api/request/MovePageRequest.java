package com.xuannie.devatlas.page.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MovePageRequest {
    @NotNull(message = "A new parent page must exist when trying to move a page under it.")
    private Long parentId;

    public MovePageRequest(Long parentId) {
        this.parentId = parentId;
    }
}
