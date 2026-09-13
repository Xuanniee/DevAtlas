package com.xuannie.devatlas.page.adapter;

import com.xuannie.devatlas.page.api.request.*;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.application.PageService;
import com.xuannie.devatlas.page.common.command.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/{workspaceId}/pages")
public class PageController {
    @Autowired
    private PageService pageService;

    // Refers to creating a Child Page under this Page
    @PostMapping("")
    public PageResponse createPage(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @Valid @RequestBody CreatePageRequest request
    ) {
        CreatePageCommand command = PageCommandBuilder.from(userId, workspaceId, request);
        return pageService.createPage(command);
    }

    // Get a Page Details
    @GetMapping("/{pageId}")
    public PageResponse getPage(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId
    ) {
        RetrievePageQuery command = PageCommandBuilder.from(userId, workspaceId, pageId);
        return pageService.getPageById(command);
    }

    // Get all Child Pages under a Page
    @GetMapping("/{pageId}/children")
    public List<PageResponse> getAllPages(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId
    ) {
        RetrievePageQuery command = PageCommandBuilder.from(userId, workspaceId, pageId);
        return pageService.findAll(command);
    }

    // Move a Page from one Parent to another (Can be same or different workspace)
    @PostMapping("/{pageId}/move")
    public PageResponse move(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId,
            @Valid @RequestBody MovePageRequest request
    ) {
        MovePageCommand command = PageCommandBuilder.from(userId, workspaceId, request);
        return pageService.movePage(command);
    }

    @PatchMapping("/{pageId}")
    public PageResponse update(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId,
            @Valid @RequestBody UpdatePageRequest request
    ) {
        UpdatePageCommand command = PageCommandBuilder.from(userId, workspaceId, pageId, request);
        return pageService.update(command);
    }

    // archive or unarchive endpoint
    @PostMapping("/{pageId}/archive")
    public PageResponse archive(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId,
            @Valid @RequestBody ArchivePageRequest request
    ) {
        return this.pageService.archive(userId, workspaceId, pageId, request);
    }

    // Return all Archive Root Pages
    @GetMapping("/archive")
    public List<PageResponse> findAllArchived(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId
    ) {
        return this.pageService.findAllArchived(userId, workspaceId);
    }

    // View one archive page
    @GetMapping("/archive/{pageId}")
    public PageResponse findArchivedById(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId
    ) {
        return this.pageService.findArchivedById(userId, workspaceId, pageId);
    }

    // View all the children pages of an archived page if any
    @GetMapping("/archive/{pageId}/children")
    public List<PageResponse> findAllArchivedChildren(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId
    ) {
        return this.pageService.findAllArchivedChildren(userId, workspaceId, pageId);
    }
}
