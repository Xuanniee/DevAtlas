package com.xuannie.devatlas.page.common.utils;

import com.xuannie.devatlas.page.common.exceptions.PageNotFoundException;
import com.xuannie.devatlas.page.common.exceptions.ParentPageNotFoundException;
import com.xuannie.devatlas.page.domain.model.Page;
import com.xuannie.devatlas.page.domain.repository.PageRepository;

public class MoveUtils {
    public MoveUtils() {}

    /**
     * A cycle will form as long as you walk up the ancestor chain from newParent
     * and see the page being moved as one of the ancestor, including newParent itself
     *
     * Must do the traversal since I can move a grandchild page under a root page
     * and all 3 pages will share the same root page, but not have cycles
     *
     * @param pageId
     * @param newParent
     * @param pageRepository
     * @return
     */
    public static boolean wouldCreatePageMoveCycles(Long ownerId, Long pageId, Page newParent, PageRepository pageRepository) {
        Long currPageId = newParent.getId();
        // Root has a parentId of null
        while (currPageId != null) {
            if (pageId.equals(currPageId)) {
                return true;
            }

            // Check if we reached the root already
            Long parentId = newParent.getParentId();
            if (parentId == null) {
                // No cycle
                return false;
            }

            // Move up the page tree
            newParent = pageRepository.findById(ownerId, newParent.getParentId())
                    .orElseThrow(() -> new ParentPageNotFoundException(pageId));
            currPageId = newParent.getId();
        }

        // No cycles
        return false;
    }
}
