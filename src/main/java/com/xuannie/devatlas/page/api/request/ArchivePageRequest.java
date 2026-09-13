package com.xuannie.devatlas.page.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ArchivePageRequest {
    @NotNull(message = "Archive Requests must have an archived status")
    private boolean archived;

    public ArchivePageRequest(boolean archived) {
        this.archived = archived;
    }
}
