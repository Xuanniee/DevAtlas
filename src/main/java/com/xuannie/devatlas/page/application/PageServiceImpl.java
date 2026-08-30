package com.xuannie.devatlas.page.application;

import com.xuannie.devatlas.page.api.request.CreatePageRequest;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.common.exceptions.PageNotFoundException;
import com.xuannie.devatlas.page.common.exceptions.RootPageAlreadyExistsException;
import com.xuannie.devatlas.page.common.mapper.PageMapper;
import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.page.domain.repository.PageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {
    private final PageRepository pageRepository;

    public PageServiceImpl(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public PageResponse createPage(CreatePageRequest request) {
        // Check if Page Name is empty
        String pageName = "Untitled";
        if (request.getName() != null) {
            // Update name
            pageName = request.getName();
        }

        // Check if the page name is already existing as cannot be duplicated
        if (!pageName.equals("Untitled") & this.pageRepository.isExistingSiblingPageByParentPage(request.getParentId(), pageName)) {
            throw new RootPageAlreadyExistsException(request.getName());
        }

        // Keep on getting the next Number if existing
        int counter = 1;
        while (pageName.equals("Untitled") & this.pageRepository.isExistingSiblingPageByParentPage(request.getParentId(), pageName)) {
            // Means already has an untitled page
            pageName = "Untitled " + counter;
            counter += 1;
        }

        // Derive the Workspace from the Parent/Root Page
        Page parentPage = this.pageRepository.findById(request.getParentId())
                .orElseThrow(() -> new PageNotFoundException(request.getParentId()));

        // PageId used in URL for pages instead of slugs
        Page page = PageMapper.toEntity(request, parentPage.getWorkspaceId());
        page.setName(pageName);

        this.pageRepository.insert(page);
        return PageMapper.toResponse(page);
    }

    /**
     * Retrieve all the Children Pages of a Parent Page.
     *
     * SQL use index instead of storing foreign reference to denormalise data
     * @param parentId
     * @return
     */
    @Override
    public List<PageResponse> getAllPages(Long parentId) {
        List<Page> childrenPages = this.pageRepository.getAllPages(parentId);

        return PageMapper.toResponseList(childrenPages);
    }

    /**
     * Get a single Page and their details
     * @param pageId
     * @return
     */
    @Override
    public PageResponse getPageById(Long pageId) {
        Page page = this.pageRepository.findById(pageId)
                .orElseThrow(() -> new PageNotFoundException(pageId));

        return PageMapper.toResponse(page);
    }
}
