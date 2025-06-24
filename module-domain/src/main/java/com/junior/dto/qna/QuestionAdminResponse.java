package com.junior.dto.qna;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record QuestionAdminResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdDateTime,
        boolean isAnswered,
        boolean isDeleted
) {

    @QueryProjection
    public QuestionAdminResponse {
    }
}
