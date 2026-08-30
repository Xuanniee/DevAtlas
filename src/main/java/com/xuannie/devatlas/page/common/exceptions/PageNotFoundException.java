package com.xuannie.devatlas.page.common.exceptions;

import com.xuannie.devatlas.common.error.NotFoundException;

public class PageNotFoundException extends NotFoundException {
    public PageNotFoundException(Long pageId) {
        super("Page with pageId " + pageId + " is not found");
    }
}
