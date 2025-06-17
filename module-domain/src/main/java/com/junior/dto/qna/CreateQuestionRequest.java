package com.junior.dto.qna;

import lombok.Builder;

@Builder
public record CreateQuestionRequest(

        String title,
        String content,
        String imgUrl

) {
}
