package com.junior.dto.member;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateNicknameDto(
        @Size(max = 25, message = "닉네임은 25자까지 가능합니다.")
        @NotNull(message = "닉네임은 필수 값입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]+$", message = "잘못된 닉네임 형식입니다.")
        String nickname
) {
}
