package com.xuannie.devatlas.page.domain.repository;

import com.xuannie.devatlas.page.domain.model.PageRevision;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PageRevisionRepository {
    int getNextRevision(Long pageId);

    void create(PageRevision pageRevision);

    List<PageRevision> findAllByPageId(Long pageId);
}
