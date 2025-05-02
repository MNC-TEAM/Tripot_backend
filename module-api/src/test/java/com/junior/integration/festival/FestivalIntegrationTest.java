package com.junior.integration.festival;

import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.festival.like.FestivalLikeRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FestivalIntegrationTest extends BaseIntegrationTest {

    private static final Clock PRESENT_CLOCK = Clock.fixed(Instant.parse("2025-01-15T10:00:00Z"), ZoneId.systemDefault());

    @SpyBean
    private Clock clock;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private FestivalLikeRepository festivalLikeRepository;

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

            if(i==1){
                festivalLikeRepository.save(FestivalLike.builder()
                        .member(activeTestMember)
                        .festival(festival)
                        .build());
            }
        }


    }

    @Test
    @DisplayName("축제 저장 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void saveFestival() throws Exception {

        //given

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/festivals")
                        .queryParam("eventStartDate", "20250101")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_CREATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_CREATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        List<Festival> result = festivalRepository.findAll();
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("축제 개최시 개수 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomUser
    void findFestivalCityCount() throws Exception {

        //given

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals/cities/count")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_CITY_COUNT_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_CITY_COUNT_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data[0].city").value("강원특별자치도"))
                .andExpect(jsonPath("$.data[0].count").value(4))
                .andExpect(jsonPath("$.data[1].city").value("서울특별시"))
                .andExpect(jsonPath("$.data[1].count").value(5))
                .andExpect(jsonPath("$.data[2].city").value("all"))
                .andExpect(jsonPath("$.data[2].count").value(9));

    }

    @Test
    @DisplayName("지도 좌표 기반 축제 리스트 조회 - 응답이 정상적으로 반환되어야 함")
    public void findFestivalByMap() throws Exception {
        //given

        Double geoPointLtY = 35.0;
        Double geoPointLtX = 125.0;
        Double geoPointRbY = 39.0;
        Double geoPointRbX = 129.0;


        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals/map")
                        .queryParam("geoPointLtY", geoPointLtY.toString())
                        .queryParam("geoPointLtX", geoPointLtX.toString())
                        .queryParam("geoPointRbY", geoPointRbY.toString())
                        .queryParam("geoPointRbX", geoPointRbX.toString())
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_MAP_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_MAP_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.length()").value(5));

    }

    @Test
    @DisplayName("축제 조회 - 응답이 정상적으로 반환되어야 함")
    public void findFestival() throws Exception {
        //given
        Integer size = 10;
        String city = "";
        String q = "";

        //when

        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals")
                        .queryParam("size", size.toString())
                        .queryParam("city", city)
                        .queryParam("q", q)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(9))
                .andExpect(jsonPath("$.data.content[1].id").value(8))
                .andExpect(jsonPath("$.data.content[2].title").value("축제 7"));


    }

    @Test
    @DisplayName("축제 조회 - 스크롤이 정상적으로 동작해야 함")
    public void findFestivalWithSlice() throws Exception {
        //given
        Long cursorId = 5L;
        Integer size = 10;
        String city = "";
        String q = "";

        //when

        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals")
                        .queryParam("cursorId", cursorId.toString())
                        .queryParam("size", size.toString())
                        .queryParam("city", city)
                        .queryParam("q", q)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(4))
                .andExpect(jsonPath("$.data.content[1].id").value(3))
                .andExpect(jsonPath("$.data.content[2].title").value("축제 2"));


    }

    @Test
    @DisplayName("축제 조회 - 검색이 정상적으로 동작해야 함")
    public void findFestivalByKeyword() throws Exception {
        //given
        Integer size = 10;
        String city = "";
        String q = "3";

        //when

        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals")
                        .queryParam("size", size.toString())
                        .queryParam("city", city)
                        .queryParam("q", q)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(3));


    }

    @Test
    @DisplayName("축제 조회 - 지역 기반 조회가 정상적으로 동작해야 함")
    public void findFestivalByCity() throws Exception {
        //given
        Integer size = 10;
        String city = "서울특별시";
        String q = "";

        //when

        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals")
                        .queryParam("size", size.toString())
                        .queryParam("city", city)
                        .queryParam("q", q)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(5));


    }

    @Test
    @DisplayName("축제 상세정보 조회 - 응답이 정상적으로 작동해야 함")
    void findFestivalDetail() throws Exception {
        //given
        Long festivalId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals/{festival_id}", festivalId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_DETAIL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_DETAIL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.detail").isNotEmpty())
                .andExpect(jsonPath("$.data.isLiked").value(false));


    }

    @Test
    @DisplayName("축제 상세정보 조회 - 좋아요 누른 축제 좋아요 여부가 정상 작동해야 함")
    @WithMockCustomUser
    void findFestivalDetailLike() throws Exception {
        //given
        Long festivalId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals/{festival_id}", festivalId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_DETAIL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_DETAIL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.detail").isNotEmpty())
                .andExpect(jsonPath("$.data.isLiked").value(true));


    }

    @Test
    @DisplayName("관리자 축제 상세정보 조회 - 응답이 정상적으로 작동해야 함")
    @WithMockCustomAdmin
    void findFestivalAdminDetail() throws Exception {
        //given
        Long festivalId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/festivals/{festival_id}", festivalId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_DETAIL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_DETAIL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.detail").isNotEmpty());

    }

    @Test
    @DisplayName("축제 관리자 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void findFestivalAdmin() throws Exception {
        //given
        Integer size = 10;
        String q = "";

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/festivals")
                        .queryParam("size", size.toString())
                        .queryParam("q", q)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(9));

    }

    @Test
    @DisplayName("축제 관리자 조회 - 검색이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void findFestivalAdminWithKeyword() throws Exception {
        //given
        Integer size = 10;
        String q = "3";

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/festivals")
                        .queryParam("size", size.toString())
                        .queryParam("q", q)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].id").value(3));

    }
}
