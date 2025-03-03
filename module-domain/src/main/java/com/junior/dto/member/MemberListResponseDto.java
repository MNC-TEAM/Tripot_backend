package com.junior.dto.member;

import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemberListResponseDto(
        Long id,
        String nickname,
        SignUpType signUpType,
        MemberStatus status,
        LocalDateTime createdDate
) {

    @QueryProjection
    public MemberListResponseDto(Long id, String nickname, SignUpType signUpType, MemberStatus status, LocalDateTime createdDate) {
        this.id = id;
        this.nickname = nickname;
        this.signUpType = signUpType;
        this.status = status;
        this.createdDate = createdDate;
    }
}
