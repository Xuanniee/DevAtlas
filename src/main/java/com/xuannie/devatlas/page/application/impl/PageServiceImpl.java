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
import com.xuannie.devatlas.workspace_members.common.constants.WorkspaceMemberConstant;
import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import com.xuannie.devatlas.workspace_members.common.exception.UnauthorisedWorkspaceMemberException;
import com.xuannie.devatlas.workspace_members.common.exception.WorkspaceMemberNotFoundException;
import com.xuannie.devatlas.workspace_members.common.utils.MembershipRoleUtils;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;
import com.xuannie.devatlas.workspace_members.domain.repository.WorkspaceMemberRepository;
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
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public PageResponse createPage(CreatePageCommand command) {
        // Verify user has the role permission to create a Page, i.e. min Admin
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                command.userId(),
                command.workspaceId(),
                WorkspaceMemberRole.ADMIN,
                WorkspaceMemberConstant.CREATE_PAGE_OPERATION
        );

        // Determine Page Name
        String pageName = "Untitled";
        if (command.name() != null) {
            pageName = command.name();
        }
        if (!pageName.equals("Untitled") & this.pageRepository.isExistingSiblingPageByParentPage(command.userId(), command.parentId(), pageName)) {
            throw new RootPageAlreadyExistsException(command.name());
        }
        int counter = 1;
        while (pageName.equals("Untitled") & this.pageRepository.isExistingSiblingPageByParentPage(command.userId(), command.parentId(), pageName)) {
            // Means already has an untitled page
            pageName = "Untitled " + counter;
            counter += 1;
        }

        // Derive the Workspace from the Parent/Root Page
        Page parentPage = this.pageRepository.findById(command.userId(), command.parentId())
                .orElseThrow(() -> new PageNotFoundException(command.parentId()));

        // PageId used in URL for pages instead of slugs
        Page page = PageMapper.toEntity(command, parentPage.getWorkspaceId());
        page.setName(pageName);

        this.pageRepository.insert(command.userId(), page);
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
        // Verify user has the role permission to read Page
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                command.userId(),
                command.workspaceId(),
                WorkspaceMemberRole.VIEWER,
                WorkspaceMemberConstant.READ_PAGE_OPERATION
        );

        // The page is the parent
        List<Page> childrenPages = this.pageRepository.findAll(command.userId(), command.pageId());

        return PageMapper.toResponseList(childrenPages);
    }

    /**
     * Get a single Page and their details
     * @param command
     * @return
     */
    @Override
    public PageResponse getPageById(RetrievePageQuery command) {
        // Verify user has the role permission to read Page
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                command.userId(),
                command.workspaceId(),
                WorkspaceMemberRole.VIEWER,
                WorkspaceMemberConstant.READ_PAGE_OPERATION
        );

        Page page = this.pageRepository.findById(command.userId(), command.pageId())
                .orElseThrow(() -> new PageNotFoundException(command.pageId()));

        return PageMapper.toResponse(page);
    }

    @Override
    public PageResponse movePage(MovePageCommand command) {
        // Retrieve the target page
        Page page = this.pageRepository.findById(command.userId(), command.currentPageId())
                .orElseThrow(() -> new PageNotFoundException(command.currentPageId()));
        // Verify user has the role permission in same workspace
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                command.userId(),
                page.getWorkspaceId(),
                WorkspaceMemberRole.ADMIN,
                WorkspaceMemberConstant.MOVE_PAGE_OPERATION
        );

        // Retrieve the New Parent Page
        Long newParentId = command.targetPageId();
        Page newParentPage = this.pageRepository.findById(command.userId(), newParentId)
                .orElseThrow(() -> new PageNotFoundException(newParentId));

        // Check if there are cycles caused by moving this page to the new parent
        if (MoveUtils.wouldCreatePageMoveCycles(command.userId(), command.currentPageId(), newParentPage, this.pageRepository)) {
            // A cycle will form
            throw new CyclicPageMoveException(command.currentPageId(), newParentId);
        }

        // Check if same or different workspace
        if (!page.getWorkspaceId().equals(newParentPage.getWorkspaceId())) {
            // Verify have permission in new workspace
            MembershipRoleUtils.validateOperationByRole(
                    this.workspaceMemberRepository,
                    command.userId(),
                    newParentPage.getWorkspaceId(),
                    WorkspaceMemberRole.ADMIN,
                    WorkspaceMemberConstant.MOVE_PAGE_OPERATION
            );
            // Update page to new workspace
            page.setWorkspaceId(newParentPage.getWorkspaceId());
        }

        // Update Parent
        page.setParentId(newParentId);

        // Update the page
        this.pageRepository.update(command.userId(), page);
        return PageMapper.toResponse(page);
    }

    @Transactional
    @Override
    public PageResponse update(UpdatePageCommand command) {
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                command.userId(),
                command.workspaceId(),
                WorkspaceMemberRole.EDITOR,
                WorkspaceMemberConstant.EDIT_PAGE_OPERATION
        );

        // Create an UpdatePage Object and populate fields from the request
        Page updatePage = this.pageRepository.findById(command.userId(), command.pageId())
                .orElseThrow(() -> new PageNotFoundException(command.pageId()));

        PatchUtils.ifPresent(command.name(), updatePage::setName);
        PatchUtils.ifPresent(command.userId(), updatePage::setOwnerId);
        PatchUtils.ifPresent(command.content(), updatePage::setContent);

        // Update Page, then create revision
        this.pageRepository.update(command.userId(), updatePage);
        CreatePageRevisionCommand revisionCommand = PageRevisionCommandBuilder.from(command.userId(), updatePage.getWorkspaceId(), updatePage.getId(), new CreatePageRevisionRequest(
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
    public PageResponse archive(Long userId, Long workspaceId, Long pageId, ArchivePageRequest request) {
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                userId,
                workspaceId,
                WorkspaceMemberRole.ADMIN,
                WorkspaceMemberConstant.ARCHIVE_PAGE_OPERATION
        );

        // Create a queue to store all the pages we need to archive/unarchive
        boolean archived = request.isArchived();
        Queue<Page> queue = new ArrayDeque<>();
        Page parentPage;

        // Find the archive root or page to be archived
        if (archived) {
            // Wants to archive
            parentPage = this.pageRepository.findById(userId, pageId)
                    .orElseThrow(() -> new PageNotFoundException(pageId));
        } else {
            parentPage = this.pageRepository.findByArchivedId(userId, pageId)
                    .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));
        }
        queue.add(parentPage);

        // Continue until all pages in subtree are archived/unarchived
        while (!queue.isEmpty()) {
            Page currPage = queue.poll();
            if (archived) {
                // Archive page and add children
                ArchiveUtils.archivePage(userId, currPage, this.pageRepository);
                queue.addAll(this.pageRepository.findAll(userId, currPage.getId()));
            } else {
                // Unarchive page and add children
                ArchiveUtils.unarchivePage(userId, currPage, this.pageRepository);
                // Reset the archived_at timestamp
                this.pageRepository.resetArchivedDatetime(userId, currPage.getId());
                currPage.setArchivedAt(null);
                queue.addAll(this.pageRepository.findAllArchivedChildren(userId, currPage.getId()));
            }
        }

        return PageMapper.toResponse(parentPage);
    }

    @Override
    public List<PageResponse> findAllArchived(Long userId, Long workspaceId) {
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                userId,
                workspaceId,
                WorkspaceMemberRole.ADMIN,
                WorkspaceMemberConstant.FIND_ARCHIVE_PAGE_OPERATION
        );

        // Retrieve all the Archive Root Pages
        List<Page> archiveRoots = this.pageRepository.findAllArchivedPages(userId);
        return PageMapper.toResponseList(archiveRoots);
    }

    @Override
    public PageResponse findArchivedById(Long userId, Long workspaceId, Long pageId) {
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                userId,
                workspaceId,
                WorkspaceMemberRole.ADMIN,
                WorkspaceMemberConstant.FIND_ARCHIVE_PAGE_OPERATION
        );

        Page archivedPage = this.pageRepository.findByArchivedId(userId, pageId)
                .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));
        return PageMapper.toResponse(archivedPage);
    }

    @Override
    public List<PageResponse> findAllArchivedChildren(Long userId, Long workspaceId, Long pageId) {
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                userId,
                workspaceId,
                WorkspaceMemberRole.ADMIN,
                WorkspaceMemberConstant.FIND_ARCHIVE_PAGE_OPERATION
        );

        Page archivedPage = this.pageRepository.findByArchivedId(userId, pageId)
                .orElseThrow(() -> new ArchivedPageNotFoundException(pageId));

        // If reach here, means must be archived
        List<Page> archivedChildren = this.pageRepository.findAllArchivedChildren(userId, pageId);
        return PageMapper.toResponseList(archivedChildren);
    }
}
