package com.xuannie.devatlas.page.application;

import com.xuannie.devatlas.page.api.request.*;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.domain.model.Page;

import java.util.List;

public interface PageService {
    PageResponse createPage(CreatePageRequest request);

    List<PageResponse> findAll(Long parentId);

    PageResponse getPageById(Long pageId);

    PageResponse movePage(Long pageId, MovePageRequest request);

    PageResponse update(Long pageId, UpdatePageRequest request);

    PageResponse archive(Long pageId, ArchivePageRequest request);

    // Every Archived Root Page is identified by archived is true, and the parent is either null or not archived
    List<PageResponse> findAllArchived();

    PageResponse findArchivedById(Long pageId);

    List<PageResponse> findAllArchivedChildren(Long pageId);
}
