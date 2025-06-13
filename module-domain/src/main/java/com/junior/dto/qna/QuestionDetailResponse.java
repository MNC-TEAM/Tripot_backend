package com.junior.dto.qna;

import com.junior.domain.qna.Question;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record QuestionDetailResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdDateTime,
        String imgUrl
) {

    public static QuestionDetailResponse from(Question question) {
        return QuestionDetailResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .createdDateTime(question.getCreatedDate())
                .imgUrl(question.getImgUrl())
                .build();
    }
}
