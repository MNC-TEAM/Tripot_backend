package com.junior.dto.jwt;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RefreshTokenDto(
        @NotNull(message = "서버 에러가 발생했습니다.")
        String refreshToken
) {
}
