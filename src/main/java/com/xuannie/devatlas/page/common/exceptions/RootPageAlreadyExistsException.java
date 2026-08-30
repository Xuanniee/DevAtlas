package com.xuannie.devatlas.page.common.exceptions;

import com.xuannie.devatlas.common.error.ConflictException;

public class RootPageAlreadyExistsException extends ConflictException {
    public RootPageAlreadyExistsException(String name) {
        super("Root page already existing with name: " + name);
    }
}
