package com.junior.integration.festival.like;

import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import com.junior.dto.festival.FestivalDto;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.festival.like.FestivalLikeRepository;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import com.junior.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        Long festivalId = 5L;

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/festivals/{festival_id}/likes", festivalId)
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
        assertThat(festivalLike.getFestival().getId()).isEqualTo(5);


    }

    @Test
    @DisplayName("축제 북마크 저장 - 이미 저장된 북마크일 경우 예외를 발생시켜야 함")
    @WithMockCustomUser
    void saveFailIfDuplicateFestivalLike() throws Exception {


        //given
        Long festivalId = 2L;

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/festivals/{festival_id}/likes", festivalId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_LIKE_DUPLICATE.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_LIKE_DUPLICATE.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));


    }

    @Test
    @DisplayName("좋아요 한 축제 조회 - 응답을 정상적으로 반환해야 함")
    @WithMockCustomUser
    void findFestivalLike() throws Exception {


        //given
        Integer size = 5;


        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals/likes")
                        .queryParam("size", size.toString())
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(4))
                .andExpect(jsonPath("$.data.content[0].id").value(4))
                .andExpect(jsonPath("$.data.content[1].id").value(3));
    }

    @Test
    @DisplayName("축제 북마크 삭제 - 응답을 정상적으로 반환해야 함")
    @WithMockCustomUser
    void delete() throws Exception {


        //given
        Long festivalId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/festivals/{festival_id}/likes", festivalId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_LIKE_DELETE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_LIKE_DELETE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));


        assertThat(festivalLikeRepository.findById(1L)).isEmpty();



    }
}
