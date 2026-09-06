package com.xuannie.devatlas.workspace.application;

import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.page.domain.repository.PageRepository;
import com.xuannie.devatlas.workspace.api.response.WorkspaceResponse;
import com.xuannie.devatlas.workspace.common.command.CreateWorkspaceCommand;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceCategory;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceVisibility;
import com.xuannie.devatlas.workspace.common.exceptions.WorkspaceAlreadyExistsException;
import com.xuannie.devatlas.workspace.common.exceptions.WorkspaceNotFoundException;
import com.xuannie.devatlas.workspace.domain.entity.Workspace;
import com.xuannie.devatlas.workspace.domain.repository.WorkspaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * A Unit Test must have Arrange, Act, Assert
 */
// Turns on Mockito annotations
@ExtendWith(MockitoExtension.class)
public class WorkspaceServiceImplTest {
    // @Mock is for Dependencies that are fake, while @InjectMocks are for the things we want to test
    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private PageRepository pageRepository;

    // Injects fake repository into constructor
    @InjectMocks
    private WorkspaceServiceImpl workspaceService;

    // Marks a method we should run and test
    @Test
    void createsWorkspaceAndPersistMappedEntity() {
        // Arrange: by preparing valid input for the service
        Long ownerId = 1L;
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(
            "Test Workspace",
                "Notes and Projects for Test Workspace",
                WorkspaceVisibility.PRIVATE,
                WorkspaceCategory.PERSONAL
        );
        // Mocks don't run real MyBatis code, so nothing simulates useGeneratedKeys
        // mutating the id onto the object unless we tell it to explicitly here.
        AtomicReference<Workspace> insertedWorkspace = new AtomicReference<>();
        doAnswer(invocation -> {
            Workspace w = invocation.getArgument(1);
            w.setId(1L);
            insertedWorkspace.set(w);
            return 1L; // insert()'s return value itself is unused by production code
        }).when(workspaceRepository).insert(eq(ownerId), any(Workspace.class));

        doAnswer(invocation -> {
            Page p = invocation.getArgument(1);
            p.setId(100L);
            return 100L;
        }).when(pageRepository).insert(eq(ownerId), any(Page.class));

        // createWorkspace() re-fetches by id at the end — give it something to find
        when(workspaceRepository.findByOwnerId(ownerId, 1L))
                .thenAnswer(invocation -> Optional.of(insertedWorkspace.get()));

        // Act: Call exactly one method on the class we are testing, i.e. the business logic we want to test
        WorkspaceResponse response = workspaceService.createWorkspace(ownerId, command);

        // ArgumentCaptor captures the exact workspace that is supplied to insert since we cannot query DB as fake
        ArgumentCaptor<Workspace> workspaceCaptor = ArgumentCaptor.forClass(Workspace.class);
        // Assert that insert was called ONCE on the mock repo and capture whatever was passed into the arguument
        verify(workspaceRepository).insert(eq(ownerId), workspaceCaptor.capture());

        // Assert: Retrieved the persisted object and assert their values were saved
        Workspace savedWorkspace = workspaceCaptor.getValue();
        assertThat(savedWorkspace.getName()).isEqualTo("Test Workspace");
        assertThat(savedWorkspace.getDescription()).isEqualTo("Notes and Projects for Test Workspace");
        assertThat(savedWorkspace.getVisibility()).isEqualTo(WorkspaceVisibility.PRIVATE);
        assertThat(savedWorkspace.getCategory()).isEqualTo(WorkspaceCategory.PERSONAL);
        // Owner ID is hardcoded to 1L at this point
        assertThat(savedWorkspace.getOwnerId()).isEqualTo(1L);

        // Ensure slug is formed, rmb UUID at end is random
        assertThat(savedWorkspace.getSlug()).startsWith("test-workspace-");

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getHomePageId()).isEqualTo(100L);
    }

    @Test
    void createDuplicateWorkspaceUnderSameOwner() {
        // Arrange: by preparing valid input for the service
        Long ownerId = 1L;
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(
                "Test Workspace",
                "Notes and Projects for Test Workspace",
                WorkspaceVisibility.PRIVATE,
                WorkspaceCategory.PERSONAL
        );

        // Fix the method to return True whenever the function is called
        when(workspaceRepository.isExistingWorkspaceNameByOwner(ownerId, "Test Workspace"))
                .thenReturn(true);

        // Act + Assert to force a conflict
        assertThatThrownBy(() -> workspaceService.createWorkspace(ownerId, command))
                .isInstanceOf(WorkspaceAlreadyExistsException.class)
                .hasMessageContaining("Test Workspace");

        // Ensure it never even attempted to insert
        verify(workspaceRepository, never()).insert(any(), any());
    }

    @Test
    void getWorkspaceByIdThrowsWhenNotFound() {
        // Arrange
        Long ownerId = 1L;
        Long workspaceId = 1L;
        when(workspaceRepository.findByOwnerId(ownerId, workspaceId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> workspaceService.getWorkspaceById(ownerId, workspaceId))
                .isInstanceOf(WorkspaceNotFoundException.class)
            .hasMessageContaining("Workspace not found with id: " + workspaceId);
    }

    @Test
    void getWorkspaceByIdReturnsWorkspaceWhenFound() {
        // Arrange — build the entity directly instead of round-tripping through createWorkspace
        Long ownerId = 1L;
        Workspace existingWorkspace = Workspace.builder()
                .id(1L)
                .name("Dummy Workspace")
                .description("This is a dummy workspace")
                .visibility(WorkspaceVisibility.PRIVATE)
                .category(WorkspaceCategory.PERSONAL)
                .ownerId(ownerId)
                .build();

        // Return the workspace if this method is called
        when(workspaceRepository.findByOwnerId(ownerId, 1L))
                .thenReturn(Optional.of(existingWorkspace));

        // Act - Call the Method
        WorkspaceResponse response = workspaceService.getWorkspaceById(ownerId, 1L);

        // Assert the Results
        assertThat(response.getName()).isEqualTo("Dummy Workspace");
        assertThat(response.getDescription()).isEqualTo("This is a dummy workspace");
        assertThat(response.getVisibility()).isEqualTo(WorkspaceVisibility.PRIVATE);
        assertThat(response.getCategory()).isEqualTo(WorkspaceCategory.PERSONAL);
    }
}
