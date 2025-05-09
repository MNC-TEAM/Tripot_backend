package com.junior.controller.festival.like;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.festival.FestivalDto;
import com.junior.exception.StatusCode;
import com.junior.security.UserPrincipal;
import com.junior.security.WithMockCustomUser;
import com.junior.service.festival.like.FestivalLikeService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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

@WebMvcTest(FestivalLikeController.class)
class FestivalLikeControllerTest extends BaseControllerTest {

    @MockBean
    FestivalLikeService festivalLikeService;

    @Test
    @DisplayName("축제 북마크 저장 - 응답을 정상적으로 반환해야 함")
    @WithMockCustomUser
    void save() throws Exception {


        //given
        Long festivalId = 1L;


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
    }

    @Test
    @DisplayName("좋아요 한 축제 조회 - 응답을 정상적으로 반환해야 함")
    @WithMockCustomUser
    void findFestivalLike() throws Exception {


        //given
        Long cursorId = 1L;
        Integer size = 5;

        Pageable pageRequest = PageRequest.of(0, size);

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

        given(festivalLikeService.findFestivalLike(anyLong(), anyInt(), any(UserPrincipal.class))).willReturn(result);


        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/festivals/likes")
                        .queryParam("cursorId", cursorId.toString())
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
                .andExpect(jsonPath("$.data.content[0].id").value(0))
                .andExpect(jsonPath("$.data.content[1].id").value(1))
                .andExpect(jsonPath("$.data.content[2].title").value("title"));
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
    }
}