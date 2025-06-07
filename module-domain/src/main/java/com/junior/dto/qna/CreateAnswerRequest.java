package com.junior.dto.qna;

import lombok.Builder;

@Builder
public record CreateAnswerRequest(
        String title,
        String content
) {
}
