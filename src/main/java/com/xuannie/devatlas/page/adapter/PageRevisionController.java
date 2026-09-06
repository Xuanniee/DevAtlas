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
@RequestMapping("/api/revisions")
public class PageRevisionController {
    @Autowired
    private PageRevisionService pageRevisionService;

    @PostMapping("/v1/create")
    public PageRevisionResponse create(
            @AuthenticationPrincipal Long ownerId,
            @Valid @RequestBody CreatePageRevisionRequest request
    ) {
        // TODO Translate the Request into a Query/Command
        CreatePageRevisionCommand command = PageRevisionCommandBuilder.from(ownerId, request);

        return this.pageRevisionService.create(command);
    }

    // Retrieve Revision Metadta
    @GetMapping("/v1/{pageId}/findAll")
    public List<PageRevisionResponse> findAllByPageId(
            @AuthenticationPrincipal Long ownerId,
            @PathVariable Long pageId
    ) {
        RetrievePageRevisionsQuery query = PageRevisionQueryBuilder.from(ownerId, pageId);
        return this.pageRevisionService.findAllByPageId(query);
    }
}
