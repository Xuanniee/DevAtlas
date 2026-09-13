package com.xuannie.devatlas.page.application.impl;

import com.xuannie.devatlas.page.api.response.PageRevisionResponse;
import com.xuannie.devatlas.page.application.PageRevisionService;
import com.xuannie.devatlas.page.common.command.CreatePageRevisionCommand;
import com.xuannie.devatlas.page.common.command.RetrievePageRevisionsQuery;
import com.xuannie.devatlas.page.common.mapper.PageRevisionMapper;
import com.xuannie.devatlas.page.domain.model.PageRevision;
import com.xuannie.devatlas.page.domain.repository.PageRevisionRepository;
import com.xuannie.devatlas.workspace_members.common.constants.WorkspaceMemberConstant;
import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import com.xuannie.devatlas.workspace_members.common.utils.MembershipRoleUtils;
import com.xuannie.devatlas.workspace_members.domain.repository.WorkspaceMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageRevisionServiceImpl implements PageRevisionService {
    @Autowired
    private PageRevisionRepository pageRevisionRepository;
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public PageRevisionResponse create(CreatePageRevisionCommand command) {
        // Verify only Editor at least can create page revisions when they update
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                command.userId(),
                command.workspaceId(),
                WorkspaceMemberRole.EDITOR,
                WorkspaceMemberConstant.CREATE_PAGE_REVISION_OPERATION
        );

        // Determine the revision number dynamically
        int revisionNumber = this.pageRevisionRepository.getNextRevision(command.userId(), command.pageId());
        // Create a Page Revision Object from the request and insert it in DB
        PageRevision revision = PageRevisionMapper.toEntity(command, revisionNumber);
        this.pageRevisionRepository.insert(command.userId(), revision);

        return PageRevisionMapper.toResponse(revision);
    }

    @Override
    public List<PageRevisionResponse> findAllByPageId(RetrievePageRevisionsQuery query) {
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                query.userId(),
                query.workspaceId(),
                WorkspaceMemberRole.VIEWER,
                WorkspaceMemberConstant.READ_PAGE_REVISION_OPERATION
        );

        List<PageRevision> revisions = this.pageRevisionRepository.findAllByPageId(query.userId(), query.pageId());
        return PageRevisionMapper.toResponseList(revisions);

    }


}
