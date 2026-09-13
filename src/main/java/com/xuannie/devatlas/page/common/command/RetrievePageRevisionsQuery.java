package com.xuannie.devatlas.page.common.command;

import com.xuannie.devatlas.common.command.Query;

public record RetrievePageRevisionsQuery(
        Long userId,
        Long workspaceId,
        Long pageId
) implements Query {}
