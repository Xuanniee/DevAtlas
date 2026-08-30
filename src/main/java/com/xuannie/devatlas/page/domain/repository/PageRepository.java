package com.xuannie.devatlas.page.domain.repository;

import com.xuannie.devatlas.page.domain.model.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PageRepository {
    Optional<Page> findById(Long pageId);

    boolean isExistingSiblingPageByParentPage(Long parentId, String pageName);

    List<Page> getAllPages(Long parentId);

    void insert(Page page);

    void update(Page page);

    void delete(Long pageId);

    Optional<Page> findByArchivedId(Long archivedPageId);

    List<Page> getAllArchivedPages(Long parentId);
}
