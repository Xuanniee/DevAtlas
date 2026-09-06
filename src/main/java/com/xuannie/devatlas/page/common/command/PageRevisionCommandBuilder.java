package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.page.api.request.CreatePageRevisionRequest;

public final class PageRevisionCommandBuilder {
    private PageRevisionCommandBuilder() {}

    public static CreatePageRevisionCommand from(Long ownerId, CreatePageRevisionRequest request) {
        return new CreatePageRevisionCommand(
                ownerId,
                request.getPageId(),
                request.getName(),
                request.getContent(),
                request.getNote(),
                request.getEditedBy()
        );
    }
}
