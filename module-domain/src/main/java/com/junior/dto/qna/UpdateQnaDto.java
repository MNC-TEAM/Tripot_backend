package com.junior.dto.qna;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateQnaDto(
        @Size(max = 255, message = "너무 긴 질문입니다.")
        @NotNull(message = "질문 항목은 필수입니다.")
        String question,
        @Size(max = 65535, message = "너무 긴 질문입니다.")
        @NotNull(message = "답변 항목은 필수입니다.")
        String answer
) {
}
