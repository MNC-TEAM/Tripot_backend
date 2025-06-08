package com.junior.repository.festival.like;

import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import com.junior.dto.festival.FestivalDto;
import com.junior.repository.BaseRepositoryTest;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

//TODO: FK로만 구성된 테이블을 테스트할 때 단위 테스트의 의미가 퇴색되지 않을까?
class FestivalLikeRepositoryTest extends BaseRepositoryTest {

    private static final Clock PRESENT_CLOCK = Clock.fixed(Instant.parse("2025-01-15T10:00:00Z"), ZoneId.systemDefault());

    @SpyBean
    private Clock clock;
    @Autowired
    private FestivalRepository festivalRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FestivalLikeRepository festivalLikeRepository;

    @BeforeEach
    void init() {

        //20250115 기준으로 테스트 진행
        given(clock.instant()).willReturn(PRESENT_CLOCK.instant());
        given(clock.getZone()).willReturn(PRESENT_CLOCK.getZone());

        Member member = createActiveTestMember();
        memberRepository.save(member);

        //각 축제는 1/1-1/31, 10번 축제부터는 조회되지 않아야 함
        for (int i = 1; i <= 18; i++) {

            Festival festival = createFestival("축제 " + i, i % 2 == 1 ? "서울특별시" : "강원특별자치도", i % 2 == 1 ? 37.0 : 40.0, i % 2 == 1 ? 125.0 : 130.0,
                    i <= 9 ? LocalDate.of(2025, 1, 1) : LocalDate.of(2025, 2, 1),
                    i <= 9 ? LocalDate.of(2025, 1, 31) : LocalDate.of(2025, 2, 28));

            festivalRepository.save(festival);

            if (i <= 6) {
                festivalLikeRepository.save(FestivalLike.builder()
                        .member(member)
                        .festival(festival)
                        .build());
            }
        }
    }

    @Test
    @DisplayName("좋아요 한 축제 조회 - 기능이 정상 동작해야 함")
    void findFestivalLike() {

        //given
        Member member = memberRepository.findById(1L).orElseThrow(RuntimeException::new);
        int size = 5;
        PageRequest pageRequest = PageRequest.of(0, size);

        //when
        Slice<FestivalDto> festivalLike = festivalLikeRepository.findFestivalLike(null, pageRequest, member);


        //then
        List<FestivalDto> content = festivalLike.getContent();

        assertThat(content.size()).isEqualTo(5);
        assertThat(content.get(0).id()).isEqualTo(6);

    }

    @Test
    @DisplayName("좋아요 한 축제 조회 - 슬라이싱 기능이 정상 동작해야 함")
    void findFestivalLikeSlice() {

        //given
        Member member = memberRepository.findById(1L).orElseThrow(RuntimeException::new);
        Long cursorId = 2L;
        int size = 5;
        PageRequest pageRequest = PageRequest.of(0, size);

        //when
        Slice<FestivalDto> festivalLike = festivalLikeRepository.findFestivalLike(cursorId, pageRequest, member);


        //then
        List<FestivalDto> content = festivalLike.getContent();

        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).id()).isEqualTo(1);

    }
}