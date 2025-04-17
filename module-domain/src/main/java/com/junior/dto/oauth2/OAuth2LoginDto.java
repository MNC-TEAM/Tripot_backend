package com.junior.dto.oauth2;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OAuth2LoginDto(
        @NotNull(message = "서버 에러가 발생했습니다.")
        String id,
        String nickname
) {
}
