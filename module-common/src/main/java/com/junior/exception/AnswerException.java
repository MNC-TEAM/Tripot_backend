package com.junior.exception;

import lombok.Getter;

@Getter
public class AnswerException extends CustomException {
    private StatusCode statusCode;

    public AnswerException(StatusCode statusCode) {
        super(statusCode);
    }
}