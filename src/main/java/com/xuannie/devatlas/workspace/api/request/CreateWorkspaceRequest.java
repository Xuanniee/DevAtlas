package com.xuannie.devatlas.workspace.api.request;

import com.xuannie.devatlas.workspace.common.enums.WorkspaceCategory;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateWorkspaceRequest {
    @NotBlank(message = "Workspace name is required")
    @Size(max = 256, message = "Workspace name must be under 256 characters")
    private String name;

    @Size(max = 512, message = "Workspace description must be under 512 characters")
    private String description;

    @NotNull(message = "Workspace visibility is required")
    private WorkspaceVisibility visibility;

    @NotNull(message = "Workspace category is required")
    private WorkspaceCategory category;

    public CreateWorkspaceRequest(
            String name,
            String description,
            WorkspaceVisibility visibility,
            WorkspaceCategory category) {
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.category = category;
    }
}


