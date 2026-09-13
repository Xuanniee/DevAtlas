package com.xuannie.devatlas.workspace.adapter;

import com.xuannie.devatlas.common.adapter.advice.GlobalExceptionHandler;
import com.xuannie.devatlas.workspace.api.response.WorkspaceResponse;
import com.xuannie.devatlas.workspace.application.WorkspaceServiceImpl;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceCategory;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceStatus;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceVisibility;
import com.xuannie.devatlas.workspace.common.exceptions.WorkspaceAlreadyExistsException;
import com.xuannie.devatlas.workspace.common.exceptions.WorkspaceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest boots only the web layer
@WebMvcTest(WorkspaceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class WorkspaceControllerTest {
    // MockMvc is a fake HTTP client that talks to Spring's DispatcherServlet in-process
    // no real socket, no real port, no real server thread.
    @Autowired
    private MockMvc mockMvc;

    // Place a Mockito mock into the application context, registered as the WorkspaceService bean
    @MockitoBean
    private WorkspaceServiceImpl workspaceService;

    private static final Long OWNER_ID = 1L;

    @BeforeEach
    void seedAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(OWNER_ID, null, List.of())
        );
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    // Test a successful Workspace Creation
    @Test
    void createsWorkspaceAndReturn201() throws Exception {
        WorkspaceResponse response = new WorkspaceResponse(
            42L,
         "test-workspace-abc12345",
        "Test Workspace",
    "Notes and Projects for Test Workspace",
                WorkspaceStatus.ACTIVE,
                WorkspaceVisibility.PRIVATE,
                WorkspaceCategory.PERSONAL,
                1L,
                null,
                LocalDateTime.now()
        );

        // When this method is called with any argument, return this response
        when(workspaceService.createWorkspace(eq(OWNER_ID), any())).thenReturn(response);

        // Simulates an actual HTTP Post request with body
        mockMvc.perform(post("/api/workspaces")
                .contentType(APPLICATION_JSON)
                .content("""
                  {
                    "name": "Test Workspace",
                    "description": "Notes and Projects for Test Workspace",
                    "visibility": "PRIVATE",
                    "category": "PERSONAL"
                  }
                """))
                .andExpect(status().isCreated())
                // Parses the JSON and assert the serialised value is correct
                .andExpect(jsonPath("$.id").value(42L))
                .andExpect(jsonPath("$.visibility").value("PRIVATE"))
                .andExpect(jsonPath("$.category").value("PERSONAL"))
                .andExpect(jsonPath("$.name").value("Test Workspace"))
                .andExpect(jsonPath("$.description").value("Notes and Projects " +
                        "for Test Workspace"));

        // Ensure the Controller is delegated to the service
        verify(workspaceService).createWorkspace(eq(OWNER_ID), any());
    }

    // Test to ensure @Valid prevents request from being processed
    @Test
    void rejectBlankWorkspaceName() throws Exception {
        // Simulates an actual HTTP Post request with body
        mockMvc.perform(post("/api/workspaces")
                        .contentType(APPLICATION_JSON)
                        .content("""
                  {
                    "name": "",
                    "description": "Notes and Projects for Test Workspace",
                    "visibility": "PRIVATE",
                    "category": "PERSONAL"
                  }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name")
                        .value("Workspace name is required"));

        // Ensure the Controller is delegated to the service
        verifyNoInteractions(workspaceService);
    }

    // Test if 409 Conflict will return if you try to create another workspace with same name
    @Test
    void duplicateWorkspaceNameAndReturn409() throws Exception {
        // Arrange: stub the mock FIRST, before the request that triggers it
        String workspaceName = "Duplicate Workspace";
        when(workspaceService.createWorkspace(eq(OWNER_ID), any()))
                .thenThrow(new WorkspaceAlreadyExistsException(workspaceName));

        // Act + Assert: simulate the actual HTTP POST request with body
        mockMvc.perform(post("/api/workspaces")
                        .contentType(APPLICATION_JSON)
                        .content("""
                      {
                        "name": "Duplicate Workspace",
                        "description": "This workspace should not be created because the name is existing",
                        "visibility": "PRIVATE",
                        "category": "PERSONAL"
                      }
                    """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail")
                        .value("Workspace already existing with name: " + workspaceName));
    }

    @Test
    void retrieveWorkspaceAndReturn404() throws Exception {
        // Arrange: Stub mock first, then write request that triggers it
        Long workspaceId = 1L;
        // Tell it to throw only if they want to request for this particular workspace to simulate not found for
        // workspaceId 1L only
        when(workspaceService.getWorkspaceById(OWNER_ID, workspaceId))
                .thenThrow(new WorkspaceNotFoundException(workspaceId));

        // Act + Assert the failure by passing in workspaceId to path
        mockMvc.perform(get("/api/workspaces/{workspaceId}", workspaceId))
                .andExpect(status().isNotFound())
                // was: jsonPath("$.message")
                .andExpect(jsonPath("$.detail")
                        .value("Workspace not found with id: " + workspaceId));
    }

}
