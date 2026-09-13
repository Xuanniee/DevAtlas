package com.xuannie.devatlas.page.adapter;

import com.xuannie.devatlas.page.api.request.CreatePageRevisionRequest;
import com.xuannie.devatlas.page.api.response.PageRevisionResponse;
import com.xuannie.devatlas.page.application.PageRevisionService;
import com.xuannie.devatlas.page.common.command.CreatePageRevisionCommand;
import com.xuannie.devatlas.page.common.command.PageRevisionCommandBuilder;
import com.xuannie.devatlas.page.common.command.PageRevisionQueryBuilder;
import com.xuannie.devatlas.page.common.command.RetrievePageRevisionsQuery;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{workspaceId}/pages")
public class PageRevisionController {
    @Autowired
    private PageRevisionService pageRevisionService;

    @PostMapping("/v1/{pageId}/revisions/create")
    public PageRevisionResponse create(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId,
            @Valid @RequestBody CreatePageRevisionRequest request
    ) {
        CreatePageRevisionCommand command = PageRevisionCommandBuilder.from(userId, workspaceId, pageId, request);
        return this.pageRevisionService.create(command);
    }

    // Retrieve Revision Metadta
    @GetMapping("/v1/{pageId}/revisions/findAll")
    public List<PageRevisionResponse> findAllByPageId(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long pageId
    ) {
        RetrievePageRevisionsQuery query = PageRevisionQueryBuilder.from(userId, workspaceId, pageId);
        return this.pageRevisionService.findAllByPageId(query);
    }
}
