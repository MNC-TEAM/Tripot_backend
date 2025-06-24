package com.junior.dto.qna;

import lombok.Builder;

@Builder
public record UpdateQuestionRequest(
        String title,
        String content,
        String imgUrl
) {
}
