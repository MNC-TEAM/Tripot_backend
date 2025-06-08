package com.junior.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberListResponseDto(
        Long id,
        String nickname,
        SignUpType signUpType,
        MemberStatus status,
        LocalDateTime createdDate,
        MemberRole role,
        boolean isWithdraw,
        LocalDateTime withdrawDate
) {

    @QueryProjection
    public MemberListResponseDto(Long id, String nickname, SignUpType signUpType, MemberStatus status, LocalDateTime createdDate, MemberRole role, boolean isWithdraw, LocalDateTime withdrawDate) {
        this.id = id;
        this.nickname = nickname;
        this.signUpType = signUpType;
        this.status = status;
        this.createdDate = createdDate;
        this.role = role;
        this.isWithdraw = isWithdraw;
        this.withdrawDate = isWithdraw ? withdrawDate : null;
    }
}
