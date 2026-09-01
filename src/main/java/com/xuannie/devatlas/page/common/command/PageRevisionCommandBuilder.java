package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.page.api.request.CreatePageRevisionRequest;

public final class PageRevisionCommandBuilder {
    private PageRevisionCommandBuilder() {}

    public static CreatePageRevisionCommand from(CreatePageRevisionRequest request) {
        return new CreatePageRevisionCommand(
                request.getPageId(),
                request.getName(),
                request.getContent(),
                request.getNote(),
                request.getEditedBy()
        );
    }
}
