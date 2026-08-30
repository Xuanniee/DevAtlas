package com.xuannie.devatlas.page.common.exceptions;

import com.xuannie.devatlas.common.error.NotFoundException;

public class ArchivedPageNotFoundException extends NotFoundException {
    public ArchivedPageNotFoundException(Long pageId) {
        super("Page " + pageId + " is either not an archived page or an unexpected error has deleted it from DB.");
    }
}
