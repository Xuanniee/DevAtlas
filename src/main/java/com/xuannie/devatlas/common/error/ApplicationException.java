package com.xuannie.devatlas.common.error;

public abstract class ApplicationException extends RuntimeException {
    protected ApplicationException(String message) { super(message); }
}
