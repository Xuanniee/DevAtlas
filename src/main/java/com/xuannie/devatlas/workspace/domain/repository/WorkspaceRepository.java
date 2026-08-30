package com.xuannie.devatlas.workspace.domain.repository;

import com.xuannie.devatlas.workspace.domain.entity.Workspace;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

/**
 * Defines the DB Operations for Workspace
 * While MyBatis have the actual implementations
 */
@Mapper
public interface WorkspaceRepository {
    Optional<Workspace> findById(Long workspaceId);

    Optional<Workspace> findBySlug(String slug);

    List<Workspace> findAll(Long ownerId);

    // Check if a Workspace has an existing slug
    boolean existsBySlug(String slug);

    // Check if the same owner has created a Workspace with this name before
    boolean isExistingWorkspaceNameByOwner(Long ownerId, String name);

    void insert(Workspace workspace);

    void update(Workspace workspace);

    void deleteById(Long id);
}
