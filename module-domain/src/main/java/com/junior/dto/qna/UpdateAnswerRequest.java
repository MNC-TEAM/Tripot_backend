package com.junior.dto.qna;

import lombok.Builder;

@Builder
public record UpdateAnswerRequest(
        String title,
        String content
) {
}
