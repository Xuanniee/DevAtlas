package com.xuannie.devatlas.page.domain.repository;

import com.xuannie.devatlas.page.domain.model.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PageRepository {
    Optional<Page> findById(
            @Param("ownerId") Long ownerId,
            @Param("pageId") Long pageId
    );

    boolean isExistingSiblingPageByParentPage(
            @Param("ownerId") Long ownerId,
            @Param("parentId") Long parentId,
            @Param("pageName") String pageName
    );

    List<Page> findAll(
            @Param("ownerId") Long ownerId,
            @Param("parentId") Long parentId
    );

    void insert(
            @Param("ownerId") Long ownerId,
            @Param("page") Page page
    );

    void update(
            @Param("ownerId") Long ownerId,
            @Param("page") Page page
    );

    void delete(
            @Param("ownerId") Long ownerId,
            @Param("pageId") Long pageId
    );

    Optional<Page> findByArchivedId(
            @Param("ownerId") Long ownerId,
            @Param("archivedPageId") Long archivedPageId
    );

    List<Page> findAllArchivedPages(@Param("ownerId") Long ownerId);

    List<Page> findAllArchivedChildren(
            @Param("ownerId") Long ownerId,
            @Param("pageId") Long pageId
    );

    void resetArchivedDatetime(
            @Param("ownerId") Long ownerId,
            @Param("pageId") Long pageId
    );
}
