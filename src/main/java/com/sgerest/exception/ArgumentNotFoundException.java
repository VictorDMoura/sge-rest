package com.sgerest.exception;

public class ArgumentNotFoundException extends RuntimeException {
    public ArgumentNotFoundException(String message) {
        super(message);
    }
}
