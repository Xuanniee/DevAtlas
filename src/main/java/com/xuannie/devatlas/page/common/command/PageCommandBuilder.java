package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.page.api.request.CreatePageRequest;
import com.xuannie.devatlas.page.api.request.MovePageRequest;
import com.xuannie.devatlas.page.api.request.UpdatePageRequest;

public final class PageCommandBuilder {
    private PageCommandBuilder() {}

    public static CreatePageCommand from(Long userId, Long workspaceId, CreatePageRequest request) {
        return new CreatePageCommand(
                request.getName(),
                userId,
                request.getParentId(),
                workspaceId,
                request.getContent()
        );
    }

    public static UpdatePageCommand from(Long userId, Long workspaceId, Long pageId, UpdatePageRequest request) {
        return new UpdatePageCommand(
                pageId,
                request.getName(),
                request.getContent(),
                userId,
                workspaceId,
                request.getUpdateNote()
        );
    }

    public static MovePageCommand from(Long userId, Long workspaceId, MovePageRequest request) {
        return new MovePageCommand(userId, workspaceId, request.getCurrParentId(), request.getNewParentId());
    }

    public static RetrievePageQuery from(Long userId, Long workspaceId, Long pageId) {
        return new RetrievePageQuery(userId, workspaceId, pageId);
    }

}
