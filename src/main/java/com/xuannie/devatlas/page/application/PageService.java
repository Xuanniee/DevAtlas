package com.xuannie.devatlas.page.application;

import com.xuannie.devatlas.page.api.request.*;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.common.command.*;

import java.util.List;

public interface PageService {
    PageResponse createPage(CreatePageCommand command);

    List<PageResponse> findAll(RetrievePageQuery command);

    PageResponse getPageById(RetrievePageQuery command);

    PageResponse movePage(MovePageCommand command);

    PageResponse update(UpdatePageCommand command);

    PageResponse archive(Long userId, Long workspaceId, Long pageId, ArchivePageRequest request);

    // Every Archived Root Page is identified by archived is true, and the parent is either null or not archived
    List<PageResponse> findAllArchived(Long userId, Long workspaceId);

    PageResponse findArchivedById(Long userId, Long workspaceId, Long pageId);

    List<PageResponse> findAllArchivedChildren(Long userId, Long workspaceId, Long pageId);
}
