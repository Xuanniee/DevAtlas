package com.xuannie.devatlas.page.application.impl;

import com.xuannie.devatlas.page.api.response.PageRevisionResponse;
import com.xuannie.devatlas.page.application.PageRevisionService;
import com.xuannie.devatlas.page.common.command.CreatePageRevisionCommand;
import com.xuannie.devatlas.page.common.command.RetrievePageRevisionsQuery;
import com.xuannie.devatlas.page.common.mapper.PageRevisionMapper;
import com.xuannie.devatlas.page.domain.model.PageRevision;
import com.xuannie.devatlas.page.domain.repository.PageRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageRevisionServiceImpl implements PageRevisionService {
    @Autowired
    private PageRevisionRepository pageRevisionRepository;

    @Override
    public PageRevisionResponse create(CreatePageRevisionCommand command) {
        // Determine the revision number dynamically
        int revisionNumber = this.pageRevisionRepository.getNextRevision(command.ownerId(), command.pageId());

        // Create a Page Revision Object from the request and insert it in DB
        PageRevision revision = PageRevisionMapper.toEntity(command, revisionNumber);
        this.pageRevisionRepository.insert(command.ownerId(), revision);

        return PageRevisionMapper.toResponse(revision);
    }

    @Override
    public List<PageRevisionResponse> findAllByPageId(RetrievePageRevisionsQuery query) {
        List<PageRevision> revisions = this.pageRevisionRepository.findAllByPageId(query.ownerId(), query.pageId());

        return PageRevisionMapper.toResponseList(revisions);

    }


}
