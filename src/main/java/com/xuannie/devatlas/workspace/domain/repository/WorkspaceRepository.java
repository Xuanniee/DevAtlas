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
    Optional<Workspace> findByUserId(
            @Param("userId") Long userId,
            @Param("workspaceId") Long workspaceId
    );

    Optional<Workspace> findByOwnerSlug(
            @Param("userId") Long userId,
            @Param("slug") String slug
    );

    List<Workspace> findAll(@Param("userId") Long userId);

    // Check if a Workspace has an existing slug
    boolean existsBySlug(@Param("userId") Long userId, @Param("slug") String slug);

    // Check if the same owner has created a Workspace with this name before
    boolean isExistingWorkspaceNameByOwner(
            @Param("userId")Long userId,
            @Param("name") String name
    );

    // Only owner can create, update or delete their own workspace
    void insert(
            @Param("userId")Long userId,
            @Param("workspace") Workspace workspace
    );

    void update(@Param("userId")Long userId,
                @Param("workspace") Workspace workspace
    );

    void delete(@Param("userId")Long userId,
                @Param("workspaceId") Long workspaceId
    );
}
