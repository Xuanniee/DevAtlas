package com.xuannie.devatlas.workspace_members.app;

import com.xuannie.devatlas.common.utils.PatchUtils;
import com.xuannie.devatlas.workspace_members.api.response.WorkspaceMemberResponse;
import com.xuannie.devatlas.workspace_members.common.commands.CreateWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.common.commands.DeleteWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.common.commands.GetWorkspaceMemberQuery;
import com.xuannie.devatlas.workspace_members.common.commands.UpdateWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.common.constants.WorkspaceMemberConstant;
import com.xuannie.devatlas.workspace_members.common.enums.WorkspaceMemberRole;
import com.xuannie.devatlas.workspace_members.common.exception.ExistingWorkspaceMemberException;
import com.xuannie.devatlas.workspace_members.common.exception.UnauthorisedWorkspaceMemberException;
import com.xuannie.devatlas.workspace_members.common.exception.WorkspaceMemberNotFoundException;
import com.xuannie.devatlas.workspace_members.common.mapper.WorkspaceMemberMapper;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;
import com.xuannie.devatlas.workspace_members.domain.repository.WorkspaceMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public WorkspaceMemberResponse create(CreateWorkspaceMemberCommand command) {
        WorkspaceMember executingWorkspaceMember = WorkspaceMemberMapper.toExecutingEntity(command);
        WorkspaceMember targetWorkspaceMember = this.workspaceMemberRepository.findByWorkspaceId(command.targetUserId(), command.workspaceId())
                .orElseThrow(() -> new ExistingWorkspaceMemberException(command.targetUserId()));
        targetWorkspaceMember = WorkspaceMemberMapper.toTargetEntity(command);

        // Executing User must be min admin to give a role and role given at most equal executing user
        if (!executingWorkspaceMember.getRole().isAtLeast(WorkspaceMemberRole.ADMIN) ||
            !targetWorkspaceMember.getRole().isAtMost(executingWorkspaceMember.getRole())) {
            throw new UnauthorisedWorkspaceMemberException(executingWorkspaceMember.getUserId(), WorkspaceMemberConstant.WORKSPACE_MEMBER_CREATE_OPERATION);
        }

        this.workspaceMemberRepository.insert(targetWorkspaceMember);
        return WorkspaceMemberMapper.toResponse(targetWorkspaceMember);
    }

    @Override
    public WorkspaceMemberResponse update(UpdateWorkspaceMemberCommand command) {
        WorkspaceMember executingWorkspaceMember = this.workspaceMemberRepository.findByWorkspaceId(command.executingUserId(), command.workspaceId())
                .orElseThrow(() -> new WorkspaceMemberNotFoundException(command.executingUserId(), command.workspaceId()));
        WorkspaceMember targetWorkspaceMember = this.workspaceMemberRepository.findByWorkspaceId(command.targetUserId(), command.workspaceId())
                .orElseThrow(() -> new WorkspaceMemberNotFoundException(command.targetUserId(), command.workspaceId()));

        // Executing must be min admin, target existing role must not be greater than executing
        // and target new role cannot be higher than executing
        if (!executingWorkspaceMember.getRole().isAtLeast(WorkspaceMemberRole.ADMIN) ||
            !executingWorkspaceMember.getRole().isAtLeast(targetWorkspaceMember.getRole()) ||
            !command.newRole().isAtMost(executingWorkspaceMember.getRole())) {
            throw new UnauthorisedWorkspaceMemberException(executingWorkspaceMember.getUserId(), WorkspaceMemberConstant.WORKSPACE_MEMBER_UPDATE_OPERATION);
        }

        PatchUtils.ifPresent(command.newRole(), newRole -> {
            targetWorkspaceMember.setRole(newRole);
            targetWorkspaceMember.setJoinedAt(LocalDateTime.now());
        });

        this.workspaceMemberRepository.update(targetWorkspaceMember);
        return WorkspaceMemberMapper.toResponse(targetWorkspaceMember);
    }

    @Override
    public WorkspaceMemberResponse findByWorkspaceId(GetWorkspaceMemberQuery query) {
        // Executing and target must have some role in the workspace
        this.workspaceMemberRepository.findByWorkspaceId(query.executingUserId(), query.workspaceId())
                .orElseThrow(() -> new UnauthorisedWorkspaceMemberException(query.executingUserId(), WorkspaceMemberConstant.WORKSPACE_MEMBER_READ_OPERATION));
        WorkspaceMember targetWorkspaceMember = this.workspaceMemberRepository.findByWorkspaceId(query.targetUserId(), query.workspaceId())
                .orElseThrow(() -> new WorkspaceMemberNotFoundException(query.targetUserId(), query.workspaceId()));

        return WorkspaceMemberMapper.toResponse(targetWorkspaceMember);
    }

    @Override
    public WorkspaceMemberResponse delete(DeleteWorkspaceMemberCommand command) {
        WorkspaceMember executingWorkspaceMember = this.workspaceMemberRepository.findByWorkspaceId(command.executingUserId(), command.workspaceId())
                .orElseThrow(() -> new WorkspaceMemberNotFoundException(command.executingUserId(), command.workspaceId()));
        WorkspaceMember targetWorkspaceMember = this.workspaceMemberRepository.findByWorkspaceId(command.targetUserId(), command.workspaceId())
                .orElseThrow(() -> new WorkspaceMemberNotFoundException(command.targetUserId(), command.workspaceId()));

        // Verify we have enough permissions to do this
        if (!executingWorkspaceMember.getRole().isAtLeast(WorkspaceMemberRole.ADMIN) ||
        !targetWorkspaceMember.getRole().isLessThan(WorkspaceMemberRole.OWNER)) {
            // Min Admin can strip role, and not owner
            throw new UnauthorisedWorkspaceMemberException(executingWorkspaceMember.getUserId(), WorkspaceMemberConstant.WORKSPACE_MEMBER_DELETE_OPERATION);
        }

        this.workspaceMemberRepository.delete(command.targetUserId(), command.workspaceId());
        return WorkspaceMemberMapper.toResponse(targetWorkspaceMember);
    }
}
