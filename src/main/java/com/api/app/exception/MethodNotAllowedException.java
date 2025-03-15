package com.api.app.exception;

public class MethodNotAllowedException extends BusinessException{
    public MethodNotAllowedException(String message, String code, int status) {
        super(message, code, status);
    }
}
