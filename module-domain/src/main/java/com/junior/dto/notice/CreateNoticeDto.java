package com.junior.dto.notice;

public record CreateNoticeDto(
        String title,
        String content,
        Boolean isHtml
) {
}
