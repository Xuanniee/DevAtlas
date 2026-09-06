package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.page.api.request.CreatePageRequest;
import com.xuannie.devatlas.page.api.request.MovePageRequest;
import com.xuannie.devatlas.page.api.request.UpdatePageRequest;

public final class PageCommandBuilder {
    private PageCommandBuilder() {}

    public static CreatePageCommand from(Long ownerId, CreatePageRequest request) {
        return new CreatePageCommand(
                request.getName(),
                ownerId,
                request.getParentId(),
                request.getContent()
        );
    }

    public static UpdatePageCommand from(Long ownerId, Long pageId, UpdatePageRequest request) {
        return new UpdatePageCommand(
                pageId,
                request.getName(),
                request.getContent(),
                ownerId,
                request.getUpdateNote()
        );
    }

    public static MovePageCommand from(Long ownerId, MovePageRequest request) {
        return new MovePageCommand(ownerId, request.getCurrParentId(), request.getNewParentId());
    }

    public static RetrievePageQuery from(Long ownerId, Long pageId) {
        return new RetrievePageQuery(ownerId, pageId);
    }

}
