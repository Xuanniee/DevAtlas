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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private PageRepository pageRepository;

    /**
     * Retrieve all the workshops that belong to a User
     * @return
     */
    @Override
    public List<Workspace> listAllWorkspaces(Long ownerId) {
        List<Workspace> workspaces = this.workspaceRepository.findAll(ownerId);

        return workspaces;
    }

    @Override
    public WorkspaceResponse getWorkspaceById(Long ownerId, Long workspaceId) {
        Workspace workspace = this.workspaceRepository.findByOwnerId(ownerId, workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));

        return WorkspaceMapper.toResponse(workspace);
    }

    /**
     * Ensure that every owner can only create one workspace with the same name
     * A root page will be created as well when a workspace is created
     * @param ownerId
     * @param command
     * @return
     */
    // Only need transaction if there are multiple writes, thats why create Page dont have
    @Transactional
    @Override
    public WorkspaceResponse createWorkspace(Long ownerId, CreateWorkspaceCommand command) {
        // Check if this owner has created this workspace before
        if (this.workspaceRepository.isExistingWorkspaceNameByOwner(ownerId, command.name())) {
            throw new WorkspaceAlreadyExistsException(command.name());
        }

        // Generate a unique slug for the workspace
        String workspaceSlug;
        workspaceSlug = SlugUtils.generateUniqueSlug(command.name());
        while (this.workspaceRepository.existsBySlug(ownerId, workspaceSlug)) {
            // Slug is not unique, try again
            workspaceSlug = SlugUtils.generateUniqueSlug(command.name());
        }

        // Map the request to a Workspace Domain Object and insert
        Workspace workspace = WorkspaceMapper.toEntity(command, ownerId, workspaceSlug);
        // useGeneratedKeys="true" place the id in the obejct we insert
        this.workspaceRepository.insert(ownerId, workspace);

        // Root Pages have no IDs and Name always start off as Untitled
        // RootPages's ParentID will be null
        Page rootPage = Page.builder()
                .name(PageConstants.ROOT_PAGE_NAME)
                .workspaceId(workspace.getId())
                .ownerId(ownerId)
                .build();
        this.pageRepository.insert(ownerId, rootPage);

        // Update the Workspace with RootPageId
        workspace.setHomePageId(rootPage.getId());
        this.workspaceRepository.update(ownerId, workspace);

        // Retrieve the actual object to return
        Workspace createdWorkspace = this.workspaceRepository.findByOwnerId(ownerId, workspace.getId())
                .orElseThrow(() -> new WorkspaceNotFoundException(workspace.getId()));
        return WorkspaceMapper.toResponse(createdWorkspace);
    }
}
