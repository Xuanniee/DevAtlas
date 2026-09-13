package com.xuannie.devatlas.workspace_members.domain.repository;

import com.xuannie.devatlas.workspace_members.api.request.UpdateWorkspaceMemberRequest;
import com.xuannie.devatlas.workspace_members.common.commands.CreateWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.common.commands.UpdateWorkspaceMemberCommand;
import com.xuannie.devatlas.workspace_members.domain.model.WorkspaceMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface WorkspaceMemberRepository {
     // Doesnt make sense to find by userId since that should not be configurable
     Optional<WorkspaceMember> findByWorkspaceId(
             @Param("userId") Long userId,
             @Param("workspaceId") Long workspaceId
     );

     void insert(@Param("workspaceMember") WorkspaceMember workspaceMember);

     void update(@Param("updatedWorkspaceMember") WorkspaceMember updatedWorkspaceMember);

     void delete(
             @Param("userId") Long userId,
             @Param("workspaceId") Long workspaceId
     );
}
