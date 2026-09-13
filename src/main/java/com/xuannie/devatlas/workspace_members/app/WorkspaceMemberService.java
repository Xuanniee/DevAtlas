package com.xuannie.devatlas.workspace_members.app;

import com.xuannie.devatlas.workspace_members.api.response.WorkspaceMemberResponse;
import com.xuannie.devatlas.workspace_members.common.commands.CreateWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.common.commands.DeleteWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.common.commands.GetWorkspaceMemberQuery;
import com.xuannie.devatlas.workspace_members.common.commands.UpdateWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.common.exception.UnauthorisedWorkspaceMemberException;

public interface WorkspaceMemberService {
    WorkspaceMemberResponse create(CreateWorkspaceMemberCommand command);

    WorkspaceMemberResponse update(UpdateWorkspaceMemberCommand command);

    WorkspaceMemberResponse findByWorkspaceId(GetWorkspaceMemberQuery query);

    WorkspaceMemberResponse delete(DeleteWorkspaceMemberCommand command);
}
