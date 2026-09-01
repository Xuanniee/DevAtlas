package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.common.command.Query;

public record RetrievePageRevisionsQuery(
        Long pageId
) implements Query {}
