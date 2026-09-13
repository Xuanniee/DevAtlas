package com.xuannie.devatlas.page.domain.repository;

import com.xuannie.devatlas.page.domain.model.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PageRepository {
    Optional<Page> findById(
            @Param("userId") Long userId,
            @Param("pageId") Long pageId
    );

    boolean isExistingSiblingPageByParentPage(
            @Param("userId") Long userId,
            @Param("parentId") Long parentId,
            @Param("pageName") String pageName
    );

    List<Page> findAll(
            @Param("userId") Long userId,
            @Param("parentId") Long parentId
    );

    void insert(
            @Param("userId") Long userId,
            @Param("page") Page page
    );

    void update(
            @Param("userId") Long userId,
            @Param("page") Page page
    );

    void delete(
            @Param("userId") Long userId,
            @Param("pageId") Long pageId
    );

    Optional<Page> findByArchivedId(
            @Param("userId") Long userId,
            @Param("archivedPageId") Long archivedPageId
    );

    List<Page> findAllArchivedPages(@Param("userId") Long userId);

    List<Page> findAllArchivedChildren(
            @Param("userId") Long userId,
            @Param("pageId") Long pageId
    );

    void resetArchivedDatetime(
            @Param("userId") Long userId,
            @Param("pageId") Long pageId
    );
}
