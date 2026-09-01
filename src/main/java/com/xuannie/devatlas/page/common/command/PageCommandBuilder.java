package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.page.api.request.CreatePageRequest;
import com.xuannie.devatlas.page.api.request.CreatePageRevisionRequest;
import com.xuannie.devatlas.page.api.request.UpdatePageRequest;

public final class PageCommandBuilder {
    private PageCommandBuilder() {}

    public static CreatePageCommand from(CreatePageRequest request) {
        return new CreatePageCommand(
                request.getName(),
                request.getOwnerId(),
                request.getParentId(),
                request.getContent()
        );
    }

    public static UpdatePageCommand from(Long pageId, UpdatePageRequest request) {
        return new UpdatePageCommand(
                pageId,
                request.getName(),
                request.getContent(),
                request.getOwnerId(),
                request.getUpdateNote()
        );
    }

    public static RetrievePageQuery from(Long pageId) {
        return new RetrievePageQuery(pageId);
    }
}
