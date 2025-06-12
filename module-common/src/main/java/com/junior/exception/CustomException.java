package com.junior.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final StatusCode statusCode;

    public CustomException(StatusCode statusCode) {
        super(statusCode.getCustomMessage());
        this.statusCode = statusCode;
    }
}