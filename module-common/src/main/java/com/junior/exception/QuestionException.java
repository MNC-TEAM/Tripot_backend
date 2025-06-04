package com.junior.exception;

import lombok.Getter;

@Getter
public class QuestionException extends RuntimeException {
    private StatusCode statusCode;

    public QuestionException(StatusCode statusCode) {
        super(statusCode.getCustomMessage());
        this.statusCode = statusCode;
    }
}