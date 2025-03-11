package com.junior.dto.notice;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NoticeUserDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdDate,
        Boolean isHtml
) {

    @QueryProjection
    public NoticeUserDto(Long id, String title, String content, LocalDateTime createdDate, Boolean isHtml) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.isHtml = isHtml;
    }
}
