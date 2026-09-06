package com.xuannie.devatlas.page.domain.repository;

import com.xuannie.devatlas.page.domain.model.PageRevision;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PageRevisionRepository {
    int getNextRevision(
            @Param("ownerId") Long ownerId,
            @Param("pageId") Long pageId
    );

    void insert(
            @Param("ownerId") Long ownerId,
            @Param("pageRevision") PageRevision pageRevision
    );

    List<PageRevision> findAllByPageId(
            @Param("ownerId") Long ownerId,
            @Param("pageId") Long pageId
    );
}
