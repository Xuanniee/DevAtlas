package com.xuannie.devatlas.common.error;

public abstract class ConflictException extends ApplicationException {
    protected ConflictException(String message) {
        super(message);
    }
}
