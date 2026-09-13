package com.xuannie.devatlas.user.common.exception;

import com.xuannie.devatlas.common.error.ApplicationException;

public class InvalidCredentialsException extends ApplicationException {
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
