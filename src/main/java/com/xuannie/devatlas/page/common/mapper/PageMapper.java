package com.xuannie.devatlas.page.common.mapper;

import com.xuannie.devatlas.page.api.request.CreatePageRequest;
import com.xuannie.devatlas.page.api.response.PageResponse;
import com.xuannie.devatlas.page.common.command.CreatePageCommand;
import com.xuannie.devatlas.page.domain.model.Page;

import java.util.ArrayList;
import java.util.List;

public class PageMapper {
    public PageMapper() {}

    public static Page toEntity(CreatePageCommand command, Long workspaceId) {
        // Use builder to set attributes and let DB insert timestamps themselves
        return Page.builder()
                .name(command.name())
                .workspaceId(workspaceId)
                .parentId(command.parentId())
                .ownerId(command.ownerId())
                .content(command.content())
                .build();
    }

    public static PageResponse toResponse(Page page) {
        return new PageResponse(
            page.getId(),
            page.getName(),
            page.getWorkspaceId(),
            page.getParentId(),
            page.getOwnerId(),
            page.getContent(),
            page.getCreatedAt(),
            page.getModifiedAt(),
            page.getArchivedAt()
        );
    }

    // Map all the Pages into a List of Response
    public static List<PageResponse> toResponseList(List<Page> pages) {
        List<PageResponse> responses = new ArrayList<>();
        for (int i = 0; i < pages.size(); i += 1) {
            PageResponse response = PageMapper.toResponse(pages.get(i));
            responses.add(response);
        }

        return responses;
    }

}
