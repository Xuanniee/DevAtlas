package com.xuannie.devatlas.user.common.exception;

import com.xuannie.devatlas.common.error.NotFoundException;

public class UserDoesNotExistException extends NotFoundException {
    public UserDoesNotExistException(String email) {
        super("User with email " + email + " does not exist.");
    }

    public UserDoesNotExistException(Long userId) {
        super("User with ID " + userId + " does not exist.");
    }
}
