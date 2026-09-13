package com.xuannie.devatlas.user.common.exception;

import com.xuannie.devatlas.common.error.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {
    public EmailAlreadyExistsException(String email) {
        super("Email " + email + " has already been registed.");
    }
}
