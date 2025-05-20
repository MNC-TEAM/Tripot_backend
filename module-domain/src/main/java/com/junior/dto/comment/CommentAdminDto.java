package com.junior.dto.comment;

import lombok.Builder;

@Builder
public record CommentAdminDto(
        Long id,
        String content,
        String createdNickname,
        Boolean isDeleted
) {
}
