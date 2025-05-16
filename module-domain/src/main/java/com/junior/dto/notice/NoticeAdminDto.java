package com.junior.dto.notice;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NoticeAdminDto(
        Long id,
        String title,
        LocalDateTime createdDateTime
) {

    @QueryProjection
    public NoticeAdminDto(Long id, String title, LocalDateTime createdDateTime) {
        this.id = id;
        this.title = title;
        this.createdDateTime = createdDateTime;
    }
}
