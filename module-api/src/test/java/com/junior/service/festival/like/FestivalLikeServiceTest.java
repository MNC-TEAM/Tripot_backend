package com.junior.service.festival.like;

import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import com.junior.exception.CustomException;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.StatusCode;
import com.junior.repository.festival.like.FestivalLikeRepository;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
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

        //when
        festivalLikeService.save(principal, festivalId);

        //then
        verify(festivalLikeRepository).save(any(FestivalLike.class));


    }

    @Test
    @DisplayName("축제 북마크 저장 - 없는 회원일 경우 예외를 발생시켜야 함")
    void saveFailIfMemberNotFound() {


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


        //when, then
        assertThatThrownBy(()->festivalLikeService.save(principal, festivalId))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());

    }

    @Test
    @DisplayName("축제 북마크 저장 - 없는 축제일 경우 예외를 발생시켜야 함")
    void saveFailIfFestivalNotFound() {


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

        //when, then
        assertThatThrownBy(()->festivalLikeService.save(principal, festivalId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(StatusCode.FESTIVAL_NOT_FOUND.getCustomMessage());

    }

    @Test
    @DisplayName("좋아요 한 축제 조회 - 기능이 정상 동작해야 함")
    void findFestivalLike() throws Exception {
        //given
        Member member = createActiveTestMember();
        Festival festival = createFestival("축제 " + 1, "서울특별시",
                37.0,
                125.0,
                3113671L,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31));

        UserPrincipal principal = new UserPrincipal(member);
        Long cursorId = 2L;
        int size = 5;

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));


        //when
        festivalLikeService.findFestivalLike(cursorId, size, principal);

        //then
        verify(festivalLikeRepository).findFestivalLike(anyLong(), any(Pageable.class), any(Member.class));

    }

    @Test
    @DisplayName("축제 북마크 삭제 - 삭제 메서드가 정상 실행되어야 함")
    void delete() {


        //given
        Member member = createActiveTestMember();
        Festival festival = createFestival("축제 " + 1, "서울특별시",
                37.0,
                125.0,
                3113671L,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31));


        FestivalLike festivalLike = FestivalLike.builder()
                .member(member)
                .festival(festival)
                .build();

        UserPrincipal principal = new UserPrincipal(member);
        Long festivalId = 1L;

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));
        given(festivalRepository.findById(anyLong())).willReturn(Optional.ofNullable(festival));
        given(festivalLikeRepository.findByMemberAndFestival(any(Member.class), any(Festival.class))).willReturn(Optional.ofNullable(festivalLike));

        //when
        festivalLikeService.delete(principal, festivalId);


        //then
        verify(festivalLikeRepository).delete(any(FestivalLike.class));


    }


    @Test
    @DisplayName("축제 북마크 삭제 - 회원이 없을 경우 예외를 발생시켜야 함")
    void deleteFailIfMemberNotFound() {


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


        //when, then
        assertThatThrownBy(()->festivalLikeService.delete(principal, festivalId))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());




    }

    @Test
    @DisplayName("축제 북마크 삭제 - 없는 축제일경우 예외를 발생시켜야 함")
    void deleteFailIfFestivalNotFound() {


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


        //when
        assertThatThrownBy(()->festivalLikeService.delete(principal, festivalId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(StatusCode.FESTIVAL_NOT_FOUND.getCustomMessage());


    }

    @Test
    @DisplayName("축제 북마크 삭제 - 좋아요가 없을 경우 예외를 발생시켜야 함")
    void deleteFailIfFestivalLikeNotFound() {


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


        //when, then
        assertThatThrownBy(()->festivalLikeService.delete(principal, festivalId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(StatusCode.FESTIVAL_LIKE_NOT_FOUND.getCustomMessage());


    }
}