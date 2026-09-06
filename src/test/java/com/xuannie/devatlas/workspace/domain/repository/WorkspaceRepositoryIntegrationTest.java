package com.xuannie.devatlas.workspace.domain.repository;

import com.xuannie.devatlas.workspace.common.enums.WorkspaceCategory;
import com.xuannie.devatlas.workspace.common.enums.WorkspacePermissions;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceStatus;
import com.xuannie.devatlas.workspace.common.enums.WorkspaceVisibility;
import com.xuannie.devatlas.workspace.domain.entity.Workspace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boots the entire Spring Application Context
 */
@SpringBootTest
@Testcontainers
public class WorkspaceRepositoryIntegrationTest {
    // Pull the mysql image to spin a temporary container image
    @Container
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("devatlas")
            .withUsername("devatlas_user")
            .withPassword("devatlas_password");
    // Configure the DB Container
    @DynamicPropertySource
    static void configureMySqlDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    // Inject dependencies
    @Autowired
    private WorkspaceRepository workspaceRepository;
    // Inject the SQL Template
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Write the Test to insert workspace and read from DB
    @Test
    void insertsAndLoadWorkspace() {
        // Add a Test User
        jdbcTemplate.update("""
            INSERT INTO users (name, email)
            VALUES (?, ?) 
            """, "Integration Test User", "workspace-test@gmail.com"
        );

        // Check if the test user is inside
        Long ownerId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE name = ?",
                Long.class,
                "Integration Test User"
        );

        // Create a Workspace and insert into temp contaner
        Workspace integrationTestWorkspace = new Workspace(
            null,
            "integration-test-workspace",
            "Integration Test Workspace",
            "Verifies MyBatis insert SQL",
            WorkspaceStatus.ACTIVE,
            WorkspaceVisibility.PRIVATE,
            WorkspaceCategory.PERSONAL,
            null,
            ownerId,
            WorkspacePermissions.ADMIN,
            LocalDateTime.now(),
            LocalDateTime.now(),
            null
        );

        // Assert that Workspace is inserted
        workspaceRepository.insert(ownerId, integrationTestWorkspace);

        assertThat(integrationTestWorkspace.getId()).isNotNull();

        Optional<Workspace> loaded =
                workspaceRepository.findByOwnerId(ownerId, integrationTestWorkspace.getId());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName())
                .isEqualTo("Integration Test Workspace");
        assertThat(loaded.get().getOwnerId()).isEqualTo(ownerId);
    }
}
