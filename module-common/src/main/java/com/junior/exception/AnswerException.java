package com.junior.exception;

import lombok.Getter;

@Getter
public class AnswerException extends CustomException {

    public AnswerException(StatusCode statusCode) {
        super(statusCode);
    }
}