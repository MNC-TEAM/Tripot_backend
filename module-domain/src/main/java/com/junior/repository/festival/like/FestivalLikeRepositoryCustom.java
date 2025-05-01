package com.junior.repository.festival.like;

import com.junior.domain.member.Member;
import com.junior.dto.festival.FestivalDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FestivalLikeRepositoryCustom {

    Slice<FestivalDto> findFestivalLike(Long cursorId, Pageable pageable, Member member);

}
