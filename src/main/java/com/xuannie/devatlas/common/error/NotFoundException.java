package com.xuannie.devatlas.common.error;

public abstract class NotFoundException extends ApplicationException{
    protected NotFoundException(String message) {
        super(message);
    }
}
