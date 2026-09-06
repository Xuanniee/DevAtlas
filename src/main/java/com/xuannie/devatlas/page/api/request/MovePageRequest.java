package com.xuannie.devatlas.page.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MovePageRequest {
    @NotNull(message = "A new parent page must exist when trying to move a page under it.")
    private Long currParentId;

    private Long newParentId;

    public MovePageRequest(Long currParentId, Long newParentId) {
        this.currParentId = currParentId;
        this.newParentId = newParentId;
    }
}
