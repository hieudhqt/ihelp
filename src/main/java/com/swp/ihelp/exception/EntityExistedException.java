package com.swp.ihelp.exception;

public class EntityExistedException extends RuntimeException{
    public EntityExistedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityExistedException(String message) {
        super(message);
    }

    public EntityExistedException(Throwable cause) {
        super(cause);
    }
}
