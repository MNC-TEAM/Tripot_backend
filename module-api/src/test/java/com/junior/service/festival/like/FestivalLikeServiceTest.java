package com.junior.service.festival.like;

import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import com.junior.repository.festival.FestivalLikeRepository;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class FestivalLikeServiceTest extends BaseServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    FestivalRepository festivalRepository;
    @Mock
    FestivalLikeRepository festivalLikeRepository;

    @InjectMocks
    FestivalLikeService festivalLikeService;

    @Test
    @DisplayName("축제 북마크 저장 - 저장 메서드가 정상 실행되어야 함")
    void save() {


        //given
        Member member = createActiveTestMember();
        Festival festival = createFestival("축제 " + 1, "서울특별시",
                37.0,
                125.0,
                3113671L,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31));


        UserPrincipal principal = new UserPrincipal(member);
        Long festivalId = 1L;

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));
        given(festivalRepository.findById(anyLong())).willReturn(Optional.ofNullable(festival));

        festivalLikeService.save(principal, festivalId);

        verify(festivalLikeRepository).save(any(FestivalLike.class));


    }
}