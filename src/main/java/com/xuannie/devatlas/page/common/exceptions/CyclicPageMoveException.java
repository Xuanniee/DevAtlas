package com.xuannie.devatlas.page.common.exceptions;

import com.xuannie.devatlas.common.error.ConflictException;

public class CyclicPageMoveException extends ConflictException {
    public CyclicPageMoveException(Long pageId, Long newParentId) {
        super("Cannot move page " + pageId + " under page " + newParentId
                + " because it would create a cycle in the page tree");
    }
}
