package com.xuannie.devatlas.workspace_members.adapter;

import com.xuannie.devatlas.workspace_members.api.request.CreateWorkspaceMemberRequest;
import com.xuannie.devatlas.workspace_members.api.request.UpdateWorkspaceMemberRequest;
import com.xuannie.devatlas.workspace_members.api.response.WorkspaceMemberResponse;
import com.xuannie.devatlas.workspace_members.app.WorkspaceMemberService;
import com.xuannie.devatlas.workspace_members.common.commands.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaceMember")
public class WorkspaceMemberController {
    @Autowired
    private WorkspaceMemberService workspaceMemberService;

    // Give another User a MemberRole
    @PostMapping("/{workspaceId}/create")
    public WorkspaceMemberResponse create(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long workspaceId,
        @Valid @RequestBody CreateWorkspaceMemberRequest request
    ) {
        CreateWorkspaceMemberCommand command = WorkspaceMemberCommandBuilder.from(userId, workspaceId, request);
        return this.workspaceMemberService.create(command);
    }

    @PostMapping("/{workspaceId}/update")
    public WorkspaceMemberResponse update(
        @AuthenticationPrincipal Long userId,
        @PathVariable Long workspaceId,
        @Valid @RequestBody UpdateWorkspaceMemberRequest request
    ) {
        UpdateWorkspaceMemberCommand command = WorkspaceMemberCommandBuilder.from(userId, workspaceId, request);
        return this.workspaceMemberService.update(command);
    }

    @GetMapping("/{workspaceId}/{targetUserId}")
    public WorkspaceMemberResponse getMemberRole(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long targetUserId
    ) {
       GetWorkspaceMemberQuery query = WorkspaceMemberQueryBuilder.from(userId, workspaceId, targetUserId);
       return this.workspaceMemberService.findByWorkspaceId(query);
    }

    // To remove a Member, Owner not allowed to be stripped
    @DeleteMapping("/{workspaceId}/{targetUserId}/delete")
    public WorkspaceMemberResponse delete(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long workspaceId,
            @PathVariable Long targetUserId
    ) {
        DeleteWorkspaceMemberCommand command = WorkspaceMemberCommandBuilder.from(userId, workspaceId, targetUserId);
        return this.workspaceMemberService.delete(command);
    }
}
