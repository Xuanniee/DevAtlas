package com.xuannie.devatlas.page.adapter;

import com.xuannie.devatlas.page.api.request.CreatePageRequest;
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
    public PageResponse createPage(@Valid CreatePageRequest request) {
        return pageService.createPage(request);
    }

    // Get a Page Details
    @GetMapping("/{pageId}")
    public PageResponse getPage(@PathVariable Long pageId) {
        return pageService.getPageById(pageId);
    }

    // Get all Child Pages under a Page
    @GetMapping("/{pageId}/children")
    public List<PageResponse> getAllPages(@PathVariable Long parentId) {
        return pageService.getAllPages(parentId);
    }

//    // Move a Page from one Parent to another (Can be same or different workspace)
//    @PostMapping("/{pageId}/move")
//    public PageResponse movePage(@PathVariable Long pageId) {
//        return pageService.movePage();
//    }
}
