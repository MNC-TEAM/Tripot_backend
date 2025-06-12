package com.junior.dto.qna;

import com.junior.domain.qna.Answer;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnswerResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdDateTime
) {

    public static AnswerResponse from(Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .title(answer.getTitle())
                .content(answer.getContent())
                .createdDateTime(answer.getCreatedDate())
                .build();
    }
}
