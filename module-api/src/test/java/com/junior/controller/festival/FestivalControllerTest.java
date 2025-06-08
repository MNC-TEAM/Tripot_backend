package com.junior.controller.festival;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.festival.*;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser;
import com.junior.service.festival.FestivalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FestivalController.class)
class FestivalControllerTest extends BaseControllerTest {

    @MockBean
    public FestivalService festivalService;

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
    }

    @Test
    @DisplayName("축제 개최시 개수 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomUser
    void findFestivalCityCount() throws Exception {

        //given
        List<FestivalCityCountDto> festivalCityCountDto = new ArrayList<>();
        festivalCityCountDto.add(FestivalCityCountDto.builder().city("강원특별자치도").count(4).build());
        festivalCityCountDto.add(FestivalCityCountDto.builder().city("서울특별시").count(5).build());
        festivalCityCountDto.add(FestivalCityCountDto.builder().city("all").count(9).build());

        given(festivalService.findFestivalCityCount()).willReturn(festivalCityCountDto);

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
                .andExpect(jsonPath("$.data[0].count").value(4))
                .andExpect(jsonPath("$.data[1].count").value(5))
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

        List<FestivalMapDto> festivalMapDtoList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            festivalMapDtoList.add(FestivalMapDto.builder()
                    .id((long) i)
                    .lat(37.0)
                    .logt(127.0)
                    .build());
        }


        given(festivalService.findFestivalByMap(anyDouble(), anyDouble(), anyDouble(), anyDouble())).willReturn(festivalMapDtoList);
        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals/map")
                        .queryParam("geoPointLtY", geoPointLtY.toString())
                        .queryParam("geoPointLtX", geoPointLtX.toString())
                        .queryParam("geoPointRbY", geoPointRbY.toString())
                        .queryParam("geoPointRbX", geoPointRbX.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.FESTIVAL_FIND_MAP_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.FESTIVAL_FIND_MAP_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[2].lat").value(37.0));

    }

    @Test
    @DisplayName("축제 조회 - 응답이 정상적으로 반환되어야 함")
    public void findFestival() throws Exception {
        //given
        Long cursorId = 5L;
        Integer size = 10;
        String city = "";
        String q = "";

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<FestivalDto> resultList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            resultList.add(FestivalDto.builder()
                    .contentId((long) i)
                    .duration("duration")
                    .imgUrl("imgurl")
                    .location("location")
                    .title("title")
                    .id((long) i)
                    .build());
        }

        Slice<FestivalDto> result = new SliceImpl<>(resultList, pageRequest, false);

        given(festivalService.findFestival(anyLong(), anyInt(), anyString(), anyString())).willReturn(result);
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
                .andExpect(jsonPath("$.data.content[0].id").value(0))
                .andExpect(jsonPath("$.data.content[1].id").value(1))
                .andExpect(jsonPath("$.data.content[2].title").value("title"));


    }

    @Test
    @DisplayName("축제 상세조회 - 응답이 정상적으로 반환되어야 함")
    void findFestivalDetail() throws Exception {
        //given
        Long festivalId = 1L;

        FestivalDetailDto result = FestivalDetailDto.builder()
                .id(1L)
                .contentId(3113671L)
                .title("title")
                .location("location")
                .detail("detail")
                .city("city")
                .imgUrl("imgurl")
                .duration("duration")
                .isLiked(false)
                .build();

        given(festivalService.findFestivalDetail(anyLong(), any())).willReturn(result);


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
                .andExpect(jsonPath("$.data.detail").value("detail"));

    }

    @Test
    @DisplayName("축제 관리자 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    public void findFestivalAdmin() throws Exception {
        //given
        Integer size = 10;
        String q = "";

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<FestivalAdminDto> resultList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            resultList.add(FestivalAdminDto.builder()
                    .duration("duration")
                    .location("location")
                    .title("title")
                    .id((long) i)
                    .build());
        }

        PageCustom<FestivalAdminDto> result = new PageCustom<>(resultList, pageRequest, 4);

        given(festivalService.findFestivalAdmin(any(Pageable.class), anyString())).willReturn(result);
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
                .andExpect(jsonPath("$.data.content[0].id").value(0))
                .andExpect(jsonPath("$.data.content[1].id").value(1))
                .andExpect(jsonPath("$.data.content[2].title").value("title"));


    }

    @Test
    @DisplayName("관리자 축제 상세조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void findFestivalAdminDetail() throws Exception {
        //given
        Long festivalId = 1L;

        FestivalDetailAdminDto result = FestivalDetailAdminDto.builder()
                .id(1L)
                .contentId(3113671L)
                .title("title")
                .location("location")
                .detail("detail")
                .duration("duration")
                .build();

        given(festivalService.findFestivalAdminDetail(anyLong())).willReturn(result);

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
                .andExpect(jsonPath("$.data.detail").value("detail"));

    }
}