package com.xuannie.devatlas.workspace.adapter;

import com.xuannie.devatlas.workspace.api.request.CreateWorkspaceRequest;
import com.xuannie.devatlas.workspace.api.response.WorkspaceResponse;
import com.xuannie.devatlas.workspace.application.WorkspaceService;
import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.workspace.domain.entity.Workspace;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping
    public List<Workspace> listAllWorkspaces() {
        Long ownerId = 1L;
        List<Workspace> workspaces = workspaceService.listAllWorkspaces(ownerId);

        return workspaces;
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable Long workspaceId) {
        WorkspaceResponse response = workspaceService.getWorkspaceById(workspaceId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        WorkspaceResponse response = workspaceService.createWorkspace(request);
        // Return if no exceptions are thrown
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
