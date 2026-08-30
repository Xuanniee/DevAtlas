package com.xuannie.devatlas.page.adapter;

import com.xuannie.devatlas.page.api.request.*;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.application.PageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/pages")
public class PageController {
    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    // Refers to creating a Child Page under this Page
    @PostMapping()
    public PageResponse createPage(@Valid @RequestBody CreatePageRequest request) {
        return pageService.createPage(request);
    }

    // Get a Page Details
    @GetMapping("/{pageId}")
    public PageResponse getPage(@PathVariable Long pageId) {
        return pageService.getPageById(pageId);
    }

    // Get all Child Pages under a Page
    @GetMapping("/{pageId}/children")
    public List<PageResponse> getAllPages(@PathVariable Long pageId) {
        return pageService.findAll(pageId);
    }

    // Move a Page from one Parent to another (Can be same or different workspace)
    @PostMapping("/{pageId}/move")
    public PageResponse move(
            @PathVariable Long pageId,
            @Valid @RequestBody MovePageRequest request
    ) {
        return pageService.movePage(pageId, request);
    }

    @PatchMapping("/{pageId}/title")
    public PageResponse update(
            @PathVariable Long pageId,
            @Valid @RequestBody UpdatePageRequest request
    ) {
        return pageService.update(pageId, request);
    }

    // archive or unarchive endpoint
    @PostMapping("/{pageId}/archive")
    public PageResponse archive(
            @PathVariable Long pageId,
            @Valid @RequestBody ArchivePageRequest request
    ) {
        return this.pageService.archive(pageId, request);
    }

    // Return all Archive Root Pages
    @GetMapping("/archive")
    public List<PageResponse> findAllArchived() {
        return this.pageService.findAllArchived();
    }

    // View one archive page
    @GetMapping("/archive/{pageId}")
    public PageResponse findArchivedById(@PathVariable Long pageId) {
        return this.pageService.findArchivedById(pageId);
    }

    // View all the children pages of an archived page if any
    @GetMapping("/archive/{pageId}/children")
    public List<PageResponse> findAllArchivedChildren(@PathVariable Long pageId) {
        return this.pageService.findAllArchivedChildren(pageId);
    }
}
