package com.xuannie.devatlas.page.common.command;

public class PageRevisionQueryBuilder {
    private PageRevisionQueryBuilder() {}

    public static RetrievePageRevisionsQuery from(Long userId, Long workspaceId, Long pageId) {
        return new RetrievePageRevisionsQuery(userId, workspaceId, pageId);
    }
}
