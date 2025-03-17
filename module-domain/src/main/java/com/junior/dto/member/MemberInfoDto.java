package com.junior.dto.member;

import com.junior.domain.member.MemberRole;
import lombok.Builder;

@Builder
public record MemberInfoDto(
        String nickname,
        String profileImageUrl,
        MemberRole role
) {
}
