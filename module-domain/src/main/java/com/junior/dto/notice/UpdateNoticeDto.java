package com.junior.dto.notice;


public record UpdateNoticeDto(
        String title,
        String content,
        Boolean isHtml
) {
}
