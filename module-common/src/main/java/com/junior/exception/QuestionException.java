package com.junior.exception;

import lombok.Getter;

@Getter
public class QuestionException extends CustomException {

    public QuestionException(StatusCode statusCode) {
        super(statusCode);
    }
}