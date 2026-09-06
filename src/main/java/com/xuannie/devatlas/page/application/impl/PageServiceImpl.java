package com.xuannie.devatlas.page.application.impl;

import com.xuannie.devatlas.common.utils.PatchUtils;
import com.xuannie.devatlas.page.api.request.*;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.application.PageRevisionService;
import com.xuannie.devatlas.page.application.PageService;
import com.xuannie.devatlas.page.common.command.*;
import com.xuannie.devatlas.page.common.constants.PageConstants;
import com.xuannie.devatlas.page.common.exceptions.ArchivedPageNotFoundException;
import com.xuannie.devatlas.page.common.exceptions.CyclicPageMoveException;
import com.xuannie.devatlas.page.common.exceptions.PageNotFoundException;
import com.xuannie.devatlas.page.common.exceptions.RootPageAlreadyExistsException;
import com.xuannie.devatlas.page.common.mapper.PageMapper;
import com.xuannie.devatlas.page.common.utils.ArchiveUtils;
import com.xuannie.devatlas.page.common.utils.MoveUtils;
import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.page.domain.repository.PageRepository;
import com.xuannie.devatlas.page.domain.repository.PageRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

@Service
public class PageServiceImpl implements PageService {
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private PageRevisionService pageRevisionService;

    @Override
    public PageResponse createPage(CreatePageCommand command) {
        // Determine Page Name
        String pageName = "Untitled";
        if (command.name() != null) {
            pageName = command.name();
        }
        if (!pageName.equals("Untitled") & this.pageRepository.isExistingSiblingPageByParentPage(command.ownerId(), command.parentId(), pageName)) {
            throw new RootPageAlreadyExistsException(command.name());
        }
        int counter = 1;
        while (pageName.equals("Untitled") & this.pageRepository.isExistingSiblingPageByParentPage(command.ownerId(), command.parentId(), pageName)) {
            // Means already has an untitled page
            pageName = "Untitled " + counter;
            counter += 1;
        }

        // Derive the Workspace from the Parent/Root Page
        Page parentPage = this.pageRepository.findById(command.ownerId(), command.parentId())
                .orElseThrow(() -> new PageNotFoundException(command.parentId()));

        // PageId used in URL for pages instead of slugs
        Page page = PageMapper.toEntity(command, parentPage.getWorkspaceId());
        page.setName(pageName);


        this.pageRepository.insert(command.ownerId(), page);
        return PageMapper.toResponse(page);
    }

    /**
     * Retrieve all the Children Pages of a Parent Page.
     *
     * SQL use index instead of storing foreign reference to denormalise data
     * @param command
     * @return
     */
    @Override
    public List<PageResponse> findAll(RetrievePageQuery command) {
        // The page is the parent
        List<Page> childrenPages = this.pageRepository.findAll(command.ownerId(), command.pageId());

        return PageMapper.toResponseList(childrenPages);
    }

    /**
     * Get a single Page and their details
     * @param command
     * @return
     */
    @Override
    public PageResponse getPageById(RetrievePageQuery command) {
        Page page = this.pageRepository.findById(command.ownerId(), command.pageId())
                .orElseThrow(() -> new PageNotFoundException(command.pageId()));

        return PageMapper.toResponse(page);
    }

    @Override
    public PageResponse movePage(MovePageCommand command) {
        // Retrieve the target page
        Page page = this.pageRepository.findById(command.ownerId(), command.currentPageId())
                .orElseThrow(() -> new PageNotFoundException(command.currentPageId()));

        // Retrieve the New Parent Page
        Long newParentId = command.targetPageId();
        Page newParentPage = this.pageRepository.findById(command.ownerId(), newParentId)
                .orElseThrow(() -> new PageNotFoundException(newParentId));

        // Check if there are cycles caused by moving this page to the new parent
        if (MoveUtils.wouldCreatePageMoveCycles(command.ownerId(), command.currentPageId(), newParentPage, this.pageRepository)) {
            // A cycle will form
            throw new CyclicPageMoveException(command.currentPageId(), newParentId);
        }

        // Check if same or different workspace
        if (!page.getWorkspaceId().equals(newParentPage.getWorkspaceId())) {
            // New Workspace
            page.setWorkspaceId(newParentPage.getWorkspaceId());
        }
        // Update Parent
        page.setParentId(newParentId);

        // Update the page
        this.pageRepository.update(command.ownerId(), page);
        return PageMapper.toResponse(page);
    }

    @Transactional
    @Override
    public PageResponse update(UpdatePageCommand command) {
        // Create an UpdatePage Object and populate fields from the request
        Page updatePage = this.pageRepository.findById(command.ownerId(), command.pageId())
                .orElseThrow(() -> new PageNotFoundException(command.pageId()));

        PatchUtils.ifPresent(command.name(), updatePage::setName);
        PatchUtils.ifPresent(command.ownerId(), updatePage::setOwnerId);
        PatchUtils.ifPresent(command.content(), updatePage::setContent);
//        if (command.name() != null) {
//            updatePage.setName(command.name());
//        }
//        if (command.ownerId() != null) {
//            updatePage.setOwnerId(command.ownerId());
//        }
//        if (command.content() != null) {
//            updatePage.setContent(command.content());
//        }

        // Update Page, then create revision
        this.pageRepository.update(command.ownerId(), updatePage);
        CreatePageRevisionCommand revisionCommand = PageRevisionCommandBuilder.from(command.ownerId(), new CreatePageRevisionRequest(
                updatePage.getId(),
                updatePage.getName(),
                updatePage.getContent(),
                command.updateNote(),
                updatePage.getOwnerId()
        ));
        this.pageRevisionService.create(revisionCommand);
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
    public PageResponse archive(Long ownerId, Long pageId, ArchivePageRequest request) {
        // Create a queue to store all the pages we need to archive/unarchive
        boolean archived = request.isArchived();
        Queue<Page> queue = new ArrayDeque<>();
        Page parentPage;

        // Find the archive root or page to be archived
        if (archived) {
            // Wants to archive
            parentPage = this.pageRepository.findById(ownerId, pageId)
                    .orElseThrow(() -> new PageNotFoundException(pageId));
        } else {
            parentPage = this.pageRepository.findByArchivedId(ownerId, pageId)
                    .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));
        }
        queue.add(parentPage);

        // Continue until all pages in subtree are archived/unarchived
        while (!queue.isEmpty()) {
            Page currPage = queue.poll();
            if (archived) {
                // Archive page and add children
                ArchiveUtils.archivePage(ownerId, currPage, this.pageRepository);
                queue.addAll(this.pageRepository.findAll(ownerId, currPage.getId()));
            } else {
                // Unarchive page and add children
                ArchiveUtils.unarchivePage(ownerId, currPage, this.pageRepository);
                // Reset the archived_at timestamp
                this.pageRepository.resetArchivedDatetime(ownerId, currPage.getId());
                currPage.setArchivedAt(null);
                queue.addAll(this.pageRepository.findAllArchivedChildren(ownerId, currPage.getId()));
            }
        }

        return PageMapper.toResponse(parentPage);
    }

    @Override
    public List<PageResponse> findAllArchived(Long ownerId) {
        // Retrieve all the Archive Root Pages
        List<Page> archiveRoots = this.pageRepository.findAllArchivedPages(ownerId);

        return PageMapper.toResponseList(archiveRoots);
    }

    @Override
    public PageResponse findArchivedById(Long ownerId, Long pageId) {
        Page archivedPage = this.pageRepository.findByArchivedId(ownerId, pageId)
                .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));

        return PageMapper.toResponse(archivedPage);
    }

    @Override
    public List<PageResponse> findAllArchivedChildren(Long ownerId, Long pageId) {
        Page archivedPage = this.pageRepository.findByArchivedId(ownerId, pageId)
                .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));

        // If reach here, means must be archived
        List<Page> archivedChildren = this.pageRepository.findAllArchivedChildren(ownerId, pageId);
        return PageMapper.toResponseList(archivedChildren);
    }


}
