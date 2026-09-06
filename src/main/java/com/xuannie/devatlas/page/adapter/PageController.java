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
@RequestMapping("/api/pages")
public class PageController {
    @Autowired
    private PageService pageService;

    // Refers to creating a Child Page under this Page
    @PostMapping()
    public PageResponse createPage(
            @AuthenticationPrincipal Long ownerId,
            @Valid @RequestBody CreatePageRequest request
    ) {
        CreatePageCommand command = PageCommandBuilder.from(ownerId, request);
        return pageService.createPage(command);
        // return ResponseAssembler.assemble();
    }

    // Get a Page Details
    @GetMapping("/{pageId}")
    public PageResponse getPage(
            @AuthenticationPrincipal Long ownerId,
            @PathVariable Long pageId
    ) {
        RetrievePageQuery command = PageCommandBuilder.from(ownerId, pageId);
        return pageService.getPageById(command);
    }

    // Get all Child Pages under a Page
    @GetMapping("/{pageId}/children")
    public List<PageResponse> getAllPages(
            @AuthenticationPrincipal Long ownerId,
            @PathVariable Long pageId
    ) {
        RetrievePageQuery command = PageCommandBuilder.from(ownerId, pageId);
        return pageService.findAll(command);
    }

    // Move a Page from one Parent to another (Can be same or different workspace)
    @PostMapping("/{pageId}/move")
    public PageResponse move(
            @AuthenticationPrincipal Long ownerId,
            @PathVariable Long pageId,
            @Valid @RequestBody MovePageRequest request
    ) {
        MovePageCommand command = PageCommandBuilder.from(ownerId, request);
        return pageService.movePage(command);
    }

    @PatchMapping("/{pageId}")
    public PageResponse update(
            @AuthenticationPrincipal Long ownerId,
            @PathVariable Long pageId,
            @Valid @RequestBody UpdatePageRequest request
    ) {
        UpdatePageCommand command = PageCommandBuilder.from(ownerId, pageId, request);
        return pageService.update(command);
    }

    // archive or unarchive endpoint
    @PostMapping("/{pageId}/archive")
    public PageResponse archive(
            @AuthenticationPrincipal Long ownerId,
            @PathVariable Long pageId,
            @Valid @RequestBody ArchivePageRequest request
    ) {
        return this.pageService.archive(ownerId, pageId, request);
    }

    // Return all Archive Root Pages
    @GetMapping("/archive")
    public List<PageResponse> findAllArchived(@AuthenticationPrincipal Long ownerId) {
        return this.pageService.findAllArchived(ownerId);
    }

    // View one archive page
    @GetMapping("/archive/{pageId}")
    public PageResponse findArchivedById(
            @AuthenticationPrincipal Long ownerId,
            @PathVariable Long pageId
    ) {
        return this.pageService.findArchivedById(ownerId, pageId);
    }

    // View all the children pages of an archived page if any
    @GetMapping("/archive/{pageId}/children")
    public List<PageResponse> findAllArchivedChildren(
            @AuthenticationPrincipal Long ownerId,
            @PathVariable Long pageId
    ) {
        return this.pageService.findAllArchivedChildren(ownerId, pageId);
    }
}
