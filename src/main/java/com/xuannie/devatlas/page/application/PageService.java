package com.xuannie.devatlas.page.application;

import com.xuannie.devatlas.page.api.request.*;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.common.command.CreatePageCommand;
import com.xuannie.devatlas.page.common.command.MoveOrRetrievePageCommand;
import com.xuannie.devatlas.page.common.command.UpdatePageCommand;

import java.util.List;

public interface PageService {
    PageResponse createPage(CreatePageCommand command);

    List<PageResponse> findAll(MoveOrRetrievePageCommand command);

    PageResponse getPageById(MoveOrRetrievePageCommand command);

    PageResponse movePage(Long pageId, MovePageRequest request);

    PageResponse update(UpdatePageCommand command);

    PageResponse archive(Long pageId, ArchivePageRequest request);

    // Every Archived Root Page is identified by archived is true, and the parent is either null or not archived
    List<PageResponse> findAllArchived();

    PageResponse findArchivedById(Long pageId);

    List<PageResponse> findAllArchivedChildren(Long pageId);
}
