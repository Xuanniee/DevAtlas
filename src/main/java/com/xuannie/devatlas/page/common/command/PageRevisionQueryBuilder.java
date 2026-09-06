package com.xuannie.devatlas.page.common.command;

public class PageRevisionQueryBuilder {
    private PageRevisionQueryBuilder() {}

    public static RetrievePageRevisionsQuery from(Long ownerId, Long pageId) {
        return new RetrievePageRevisionsQuery(ownerId, pageId);
    }
}
