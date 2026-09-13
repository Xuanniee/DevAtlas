package com.xuannie.devatlas.page.common.exceptions;

import com.xuannie.devatlas.common.error.ApplicationException;

public class PageNotArchivedException extends ApplicationException {
    public PageNotArchivedException(Long pageId) {
        super("Failed to archive Page " + pageId + " and its children");
    }
}
