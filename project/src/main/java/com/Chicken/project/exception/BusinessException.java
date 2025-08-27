package com.Chicken.project.exception;



public class BusinessException extends RuntimeException {
    private final String errorCode;
    public BusinessException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
}
