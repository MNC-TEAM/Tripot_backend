package com.junior.dto.comment;

import java.time.LocalDateTime;

public record ResponseMyCommentDto(
        Long commentId,
        Long storyId,
        String content,
        LocalDateTime createDate,
        String storyTitle
) {
}
