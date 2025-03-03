package com.junior.repository.member;

import com.junior.dto.member.MemberListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

    public Page<MemberListResponseDto> findMember(Pageable pageable, String q);
}
