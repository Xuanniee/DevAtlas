package com.xuannie.devatlas.workspace.domain.repository;

import com.xuannie.devatlas.workspace.domain.entity.Workspace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * Defines the DB Operations for Workspace
 * While MyBatis have the actual implementations
 */
@Mapper
public interface WorkspaceRepository {
    Optional<Workspace> findByOwnerId(
            @Param("ownerId") Long ownerId,
            @Param("workspaceId") Long workspaceId
    );

    Optional<Workspace> findByOwnerSlug(
            @Param("ownerId") Long ownerId,
            @Param("slug") String slug
    );

    List<Workspace> findAll(@Param("ownerId") Long ownerId);

    // Check if a Workspace has an existing slug
    boolean existsBySlug(@Param("ownerId") Long ownerId, @Param("slug") String slug);

    // Check if the same owner has created a Workspace with this name before
    boolean isExistingWorkspaceNameByOwner(
            @Param("ownerId")Long ownerId,
            @Param("name") String name
    );

    // Only owner can create, update or delete their own workspace
    void insert(
            @Param("ownerId")Long ownerId,
            @Param("workspace") Workspace workspace
    );

    void update(@Param("ownerId")Long ownerId,
                @Param("workspace") Workspace workspace
    );

    void delete(@Param("ownerId")Long ownerId,
                @Param("workspaceId") Long workspaceId
    );
}
