package com.junior.dto.notice;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateNoticeDto(
        @Size(max = 255, message = "너무 긴 제목입니다.")
        @NotNull(message = "제목은 필수입니다.")
        String title,

        @Size(max = 65535, message = "너무 긴 내용입니다.")
        @NotNull(message = "내용은 필수입니다.")
        String content
) {
}
