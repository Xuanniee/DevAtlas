package com.xuannie.devatlas.workspace_members.common.enums;

import lombok.Getter;

@Getter
public enum WorkspaceMemberRole {
    VIEWER("VIEWER", "User can view the workspace but not edit them", 1),
    EDITOR("EDITOR", "User can view and edit workspace and pages", 2),
    ADMIN("ADMIN", "Users have the same permissions as the owner", 3),
    OWNER("OWNER", "User who created the workspace", 4);

    private final String code;
    private final String description;
    private final int accessLevel;

    // Private Constructor
    WorkspaceMemberRole(String code, String description, int accessLevel) {
        this.code = code;
        this.description = description;
        this.accessLevel = accessLevel;
    }

    // The role who invoked this isAtLeast >= Role passed into method
    public boolean isAtLeast(WorkspaceMemberRole required) {
        return this.accessLevel >= required.getAccessLevel();
    }

    public boolean isAtMost(WorkspaceMemberRole required) {
        return this.accessLevel <= required.getAccessLevel();
    }

    public boolean isLessThan(WorkspaceMemberRole required) {
        return this.accessLevel < required.getAccessLevel();
    }
}
