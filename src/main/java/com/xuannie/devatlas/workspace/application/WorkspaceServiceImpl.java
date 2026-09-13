package com.xuannie.devatlas.workspace.application;

import com.xuannie.devatlas.page.common.constants.PageConstants;
import com.xuannie.devatlas.page.domain.repository.PageRepository;
import com.xuannie.devatlas.workspace.api.request.CreateWorkspaceRequest;
import com.xuannie.devatlas.workspace.api.response.WorkspaceResponse;
import com.xuannie.devatlas.workspace.common.command.CreateWorkspaceCommand;
import com.xuannie.devatlas.workspace.common.exceptions.WorkspaceAlreadyExistsException;
import com.xuannie.devatlas.workspace.common.exceptions.WorkspaceNotFoundException;
import com.xuannie.devatlas.workspace.common.mapper.WorkspaceMapper;
import com.xuannie.devatlas.workspace.common.utils.SlugUtils;
import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.workspace.domain.entity.Workspace;
import com.xuannie.devatlas.workspace.domain.repository.WorkspaceRepository;
import java.util.List;

import com.xuannie.devatlas.workspace_members.app.WorkspaceMemberService;
import com.xuannie.devatlas.workspace_members.common.commands.CreateWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.common.commands.GetWorkspaceMemberQuery;
import com.xuannie.devatlas.workspace_members.common.commands.WorkspaceMemberCommandBuilder;
import com.xuannie.devatlas.workspace_members.common.commands.WorkspaceMemberQueryBuilder;
import com.xuannie.devatlas.workspace_members.common.constants.WorkspaceMemberConstant;
import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import com.xuannie.devatlas.workspace_members.common.exception.UnauthorisedWorkspaceMemberException;
import com.xuannie.devatlas.workspace_members.common.exception.WorkspaceMemberNotFoundException;
import com.xuannie.devatlas.workspace_members.common.mapper.WorkspaceMemberMapper;
import com.xuannie.devatlas.workspace_members.common.utils.MembershipRoleUtils;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;
import com.xuannie.devatlas.workspace_members.domain.repository.WorkspaceMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    /**
     * Retrieve all the workshops that belong to a User
     * @return
     */
    @Override
    public List<WorkspaceResponse> listAllWorkspaces(Long userId) {
        List<Workspace> workspaces = this.workspaceRepository.findAll(userId);
        return WorkspaceMapper.toResponseList(workspaces);
    }

    @Override
    public WorkspaceResponse getWorkspaceById(Long userId, Long workspaceId) {
        // Retrieve the role and ensure user has privilege to see
        MembershipRoleUtils.validateOperationByRole(
                this.workspaceMemberRepository,
                userId,
                workspaceId,
                WorkspaceMemberRole.VIEWER,
                WorkspaceMemberConstant.WORKSPACE_MEMBER_READ_OPERATION
        );

        Workspace workspace = this.workspaceRepository.findByUserId(userId, workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        return WorkspaceMapper.toResponse(workspace);
    }

    /**
     * Ensure that every owner can only create one workspace with the same name
     * A root page will be created as well when a workspace is created
     * @param userId
     * @param command
     * @return
     */
    // Only need transaction if there are multiple writes, thats why create Page dont have
    @Transactional
    @Override
    public WorkspaceResponse createWorkspace(Long userId, CreateWorkspaceCommand command) {
        // Check if this owner has created this workspace before
        if (this.workspaceRepository.isExistingWorkspaceNameByOwner(userId, command.name())) {
            throw new WorkspaceAlreadyExistsException(command.name());
        }

        // Generate a unique slug for the workspace
        String workspaceSlug;
        workspaceSlug = SlugUtils.generateUniqueSlug(command.name());
        while (this.workspaceRepository.existsBySlug(userId, workspaceSlug)) {
            // Slug is not unique, try again
            workspaceSlug = SlugUtils.generateUniqueSlug(command.name());
        }

        // Map the request to a Workspace Domain Object and insert
        Workspace workspace = WorkspaceMapper.toEntity(command, userId, workspaceSlug);
        // useGeneratedKeys="true" place the id in the obejct we insert
        this.workspaceRepository.insert(userId, workspace);

        // Root Pages have no IDs and Name always start off as Untitled
        // RootPages's ParentID will be null
        Page rootPage = Page.builder()
                .name(PageConstants.ROOT_PAGE_NAME)
                .workspaceId(workspace.getId())
                .ownerId(userId)
                .build();
        this.pageRepository.insert(userId, rootPage);

        // Update the Workspace with RootPageId
        workspace.setHomePageId(rootPage.getId());
        this.workspaceRepository.update(userId, workspace);

        // Add the User as an owner of the workspace
        WorkspaceMember ownerMembership = WorkspaceMember.builder()
                .workspaceId(workspace.getId())
                .userId(userId)
                .role(WorkspaceMemberRole.OWNER)
                .build();
        this.workspaceMemberRepository.insert(ownerMembership);

        // Retrieve the actual object to return
        Workspace createdWorkspace = this.workspaceRepository.findByUserId(userId, workspace.getId())
                .orElseThrow(() -> new WorkspaceNotFoundException(workspace.getId()));
        return WorkspaceMapper.toResponse(createdWorkspace);
    }
}
