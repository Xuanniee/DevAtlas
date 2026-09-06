package com.xuannie.devatlas.workspace.common.command;

import com.xuannie.devatlas.common.command.Command;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceCategory;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceVisibility;

public record CreateWorkspaceCommand(
    String name,
    String description,
    WorkspaceVisibility visibility,
    WorkspaceCategory category
) implements Command {}
