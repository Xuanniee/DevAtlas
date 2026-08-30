package com.xuannie.devatlas.page.application;

import com.xuannie.devatlas.page.api.request.CreatePageRequest;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.domain.model.Page;

import java.util.List;

public interface PageService {
    PageResponse createPage(CreatePageRequest request);

    List<PageResponse> getAllPages(Long parentId);

    PageResponse getPageById(Long pageId);
}
