package com.junior.controller.festival.like;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.festival.like.CreateFestivalLikeDto;
import com.junior.exception.StatusCode;
import com.junior.security.WithMockCustomUser;
import com.junior.service.festival.like.FestivalLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.nullValue;
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
    }
}