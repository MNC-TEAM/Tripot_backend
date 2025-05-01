package com.junior.integration.festival.like;

import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import com.junior.dto.festival.like.CreateFestivalLikeDto;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.festival.FestivalLikeRepository;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.security.WithMockCustomUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FestivalLikeIntegrationTest extends BaseIntegrationTest {

    private static final Clock PRESENT_CLOCK = Clock.fixed(Instant.parse("2025-01-15T10:00:00Z"), ZoneId.systemDefault());

    @Autowired
    FestivalLikeRepository festivalLikeRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FestivalRepository festivalRepository;

    @SpyBean
    private Clock clock;

    @BeforeEach
    void init() {
        //20250115 기준으로 테스트 진행
        given(clock.instant()).willReturn(PRESENT_CLOCK.instant());
        given(clock.getZone()).willReturn(PRESENT_CLOCK.getZone());

        Member preactiveTestMember = createPreactiveTestMember();
        Member activeTestMember = createActiveTestMember();
        Member testAdmin = createAdmin();
        Member activeTestMember2 = createActiveTestMember2();

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeTestMember);
        memberRepository.save(testAdmin);
        memberRepository.save(activeTestMember2);


        for (int i = 1; i <= 18; i++) {
            Festival festival = createFestival("축제 " + i, i % 2 == 1 ? "서울특별시" : "강원특별자치도",
                    i % 2 == 1 ? 37.0 : 40.0,
                    i % 2 == 1 ? 125.0 : 130.0,
                    i == 1 ? 3113671L : (long) (Math.random() * 1000),
                    i <= 9 ? LocalDate.of(2025, 1, 1) : LocalDate.of(2025, 2, 1),
                    i <= 9 ? LocalDate.of(2025, 1, 31) : LocalDate.of(2025, 2, 28));

            festivalRepository.save(festival);

            if (i <= 4) {
                FestivalLike festivalLike = createFestivalLike(activeTestMember, festival);
                festivalLikeRepository.save(festivalLike);
            }
        }


    }

    @Test
    @DisplayName("축제 북마크 저장 - 응답을 정상적으로 반환해야 함")
    @WithMockCustomUser
    void save() throws Exception {


        //given
        CreateFestivalLikeDto createFestivalLikeDto = CreateFestivalLikeDto.builder()
                .festivalId(1L)
                .build();

        String content = objectMapper.writeValueAsString(createFestivalLikeDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/festival-likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_LIKE_CREATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_LIKE_CREATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        FestivalLike festivalLike = festivalLikeRepository.findById(5L).orElseThrow(RuntimeException::new);

        assertThat(festivalLike.getMember().getId()).isEqualTo(2);
        assertThat(festivalLike.getFestival().getId()).isEqualTo(1);


    }
}
