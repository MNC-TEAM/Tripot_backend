package com.junior.dto.member;

import lombok.Builder;

@Builder
public record UpdateMemberRoleDto(
        String role
) {
}
