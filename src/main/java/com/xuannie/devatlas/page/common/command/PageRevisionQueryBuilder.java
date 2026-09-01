package com.xuannie.devatlas.page.common.command;

public class PageRevisionQueryBuilder {
    private PageRevisionQueryBuilder() {}

    public static RetrievePageRevisionsQuery from(Long pageId) {
        return new RetrievePageRevisionsQuery(pageId);
    }
}
