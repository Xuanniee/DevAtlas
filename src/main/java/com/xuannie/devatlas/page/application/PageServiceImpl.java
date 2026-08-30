package com.xuannie.devatlas.page.application;

import com.xuannie.devatlas.page.api.request.*;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.common.exceptions.ArchivedPageNotFoundException;
import com.xuannie.devatlas.page.common.exceptions.CyclicPageMoveException;
import com.xuannie.devatlas.page.common.exceptions.PageNotFoundException;
import com.xuannie.devatlas.page.common.exceptions.RootPageAlreadyExistsException;
import com.xuannie.devatlas.page.common.mapper.PageMapper;
import com.xuannie.devatlas.page.common.utils.ArchiveUtils;
import com.xuannie.devatlas.page.common.utils.MoveUtils;
import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.page.domain.repository.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

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
    public List<PageResponse> findAll(Long parentId) {
        List<Page> childrenPages = this.pageRepository.findAll(parentId);

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

    @Override
    public PageResponse movePage(Long pageId, MovePageRequest request) {
        // Retrieve the target page
        Page page = this.pageRepository.findById(pageId)
                .orElseThrow(() -> new PageNotFoundException(pageId));

        // Retrieve the New Parent Page
        Long newParentId = request.getParentId();
        Page newParentPage = this.pageRepository.findById(newParentId)
                .orElseThrow(() -> new PageNotFoundException(newParentId));

        // Check if there are cycles caused by moving this page to the new parent
        if (MoveUtils.wouldCreatePageMoveCycles(pageId, newParentPage, this.pageRepository)) {
            // A cycle will form
            throw new CyclicPageMoveException(pageId, newParentId);
        }

        // Check if same or different workspace
        if (!page.getWorkspaceId().equals(newParentPage.getWorkspaceId())) {
            // New Workspace
            page.setWorkspaceId(newParentPage.getWorkspaceId());
        }
        // Update Parent
        page.setParentId(newParentId);

        // Update the page
        this.pageRepository.update(page);
        return PageMapper.toResponse(page);
    }

    @Override
    public PageResponse update(Long pageId, UpdatePageRequest request) {
        // Create an UpdatePage Object and populate fields from the request
        Page updatePage = this.pageRepository.findById(pageId)
                .orElseThrow(() -> new PageNotFoundException(pageId));

        if (request.getName() != null) {
            updatePage.setName(request.getName());
        }

        if (request.getOwnerId() != null) {
            updatePage.setOwnerId(request.getOwnerId());
        }

        this.pageRepository.update(updatePage);
        return PageMapper.toResponse(updatePage);
    }

    /**
     * Archiving a Page, archives all the child pages under this page.
     *
     * Note that we archive pages that are not very relevant but still contain
     * accurate data to keep our space clean. Hence there is no tree or hierarchy of
     * archived pages. Each pages is archived by itself, with the oldest parent page
     * acting as the root
     * @param pageId
     * @param request
     * @return
     */
    @Transactional
    @Override
    public PageResponse archive(Long pageId, ArchivePageRequest request) {
        // Create a queue to store all the pages we need to archive/unarchive
        boolean archived = request.isArchived();
        Queue<Page> queue = new ArrayDeque<>();
        Page parentPage;

        // Find the archive root or page to be archived
        if (archived) {
            // Wants to archive
            parentPage = this.pageRepository.findById(pageId)
                    .orElseThrow(() -> new PageNotFoundException(pageId));
        } else {
            parentPage = this.pageRepository.findByArchivedId(pageId)
                    .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));
        }
        queue.add(parentPage);

        // Continue until all pages in subtree are archived/unarchived
        while (!queue.isEmpty()) {
            Page currPage = queue.poll();
            if (archived) {
                // Archive page and add children
                ArchiveUtils.archivePage(currPage, this.pageRepository);
                queue.addAll(this.pageRepository.findAll(currPage.getId()));
            } else {
                // Unarchive page and add children
                ArchiveUtils.unarchivePage(currPage, this.pageRepository);
                // Reset the archived_at timestamp
                this.pageRepository.resetArchivedDatetime(currPage.getId());
                currPage.setArchivedAt(null);
                queue.addAll(this.pageRepository.findAllArchivedChildren(currPage.getId()));
            }
        }

        return PageMapper.toResponse(parentPage);
    }

    @Override
    public List<PageResponse> findAllArchived() {
        // Retrieve all the Archive Root Pages
        List<Page> archiveRoots = this.pageRepository.findAllArchivedPages();

        return PageMapper.toResponseList(archiveRoots);
    }

    @Override
    public PageResponse findArchivedById(Long pageId) {
        Page archivedPage = this.pageRepository.findByArchivedId(pageId)
                .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));

        return PageMapper.toResponse(archivedPage);
    }

    @Override
    public List<PageResponse> findAllArchivedChildren(Long pageId) {
        Page archivedPage = this.pageRepository.findByArchivedId(pageId)
                .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));

        // If reach here, means must be archived
        List<Page> archivedChildren = this.pageRepository.findAllArchivedChildren(pageId);
        return PageMapper.toResponseList(archivedChildren);
    }


}
