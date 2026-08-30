package com.xuannie.devatlas.page.common.exceptions;

import com.xuannie.devatlas.common.error.NotFoundException;

public class ParentPageNotFoundException extends NotFoundException {
    public ParentPageNotFoundException(Long pageId) {
      super("One of the ancestor pages of " + pageId + " does not " +
              "exists when traversing up the tree.");
    }
}
