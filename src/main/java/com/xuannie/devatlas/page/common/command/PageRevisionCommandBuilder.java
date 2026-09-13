package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.page.api.request.CreatePageRevisionRequest;

public final class PageRevisionCommandBuilder {
    private PageRevisionCommandBuilder() {}

    public static CreatePageRevisionCommand from(Long userId, Long workspaceId, Long pageId, CreatePageRevisionRequest request) {
        return new CreatePageRevisionCommand(
                userId,
                workspaceId,
                pageId,
                request.getName(),
                request.getContent(),
                request.getNote(),
                request.getEditedBy()
        );
    }
}
