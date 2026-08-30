package com.xuannie.devatlas.common.error;

public abstract class ValidationException extends ApplicationException {
    protected ValidationException(String message) {
        super(message);
    }
}
