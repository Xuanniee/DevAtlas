package com.xuannie.devatlas.page.common.mapper;

import com.xuannie.devatlas.page.api.request.CreatePageRevisionRequest;
import com.xuannie.devatlas.page.api.response.PageRevisionResponse;
import com.xuannie.devatlas.page.common.command.CreatePageRevisionCommand;
import com.xuannie.devatlas.page.domain.model.PageRevision;

import java.util.ArrayList;
import java.util.List;

public class PageRevisionMapper {
    public PageRevisionMapper() {}

    public static PageRevision toEntity(CreatePageRevisionCommand command, int revisionNumber) {
        return PageRevision.builder()
                .pageId(command.pageId())
                .name(command.name())
                .content(command.content())
                .note(command.note())
                .editedBy(command.editedBy())
                .revisionNumber(revisionNumber)
                .build();
    }

    public static PageRevisionResponse toResponse(PageRevision pageRevision) {
        return new PageRevisionResponse(
                pageRevision.getPageId(),
                pageRevision.getRevisionNumber(),
                pageRevision.getName(),
                pageRevision.getContent(),
                pageRevision.getNote(),
                pageRevision.getEditedBy(),
                pageRevision.getCreatedAt()
        );
    }

    public static List<PageRevisionResponse> toResponseList(List<PageRevision> pageRevisions) {
        List<PageRevisionResponse> responses = new ArrayList<PageRevisionResponse>();
        for (PageRevision pageRevision : pageRevisions) {
            responses.add(PageRevisionMapper.toResponse(pageRevision));
        }
        return responses;
    }
}
