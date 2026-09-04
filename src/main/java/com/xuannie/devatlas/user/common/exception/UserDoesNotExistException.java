package com.xuannie.devatlas.user.common.exception;

import com.xuannie.devatlas.common.error.NotFoundException;

public class UserDoesNotExistException extends NotFoundException {
    public UserDoesNotExistException(String email) {
        super("User with email " + email + " does not exist.");
    }
}
