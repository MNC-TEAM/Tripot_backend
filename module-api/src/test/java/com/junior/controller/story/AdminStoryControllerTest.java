package com.junior.controller.story;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.story.AdminStoryDetailDto;
import com.junior.dto.story.AdminStoryDto;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.security.WithMockCustomAdmin;
import com.junior.service.story.AdminStoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminStoryController.class)
class AdminStoryControllerTest extends BaseControllerTest {


    @MockBean
    private AdminStoryService adminStoryService;


    @Test
    @DisplayName("관리자 스토리 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void findStory() throws Exception {

        //given
        PageRequest pageRequest = PageRequest.of(0, 15);

        List<AdminStoryDto> storyDtos = new ArrayList<>();

        AdminStoryDto storyDto = AdminStoryDto.builder()
                .id(1L)
                .title("title")
                .city("서울")
                .isDeleted(false)
                .createdNickname("nickname")
                .build();

        storyDtos.add(storyDto);

        given(adminStoryService.findStory(any(Pageable.class), anyString())).willReturn(new PageCustom<>(storyDtos, pageRequest, storyDtos.size()));

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/stories")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.STORY_READ_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.STORY_READ_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1))
                .andExpect(jsonPath("$.data.content[0].city").value("서울"))
                .andExpect(jsonPath("$.data.content[0].createdNickname").value("nickname"));
    }

    @Test
    @DisplayName("관리자 스토리 상세 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void findStoryDetail() throws Exception {

        //given
        AdminStoryDetailDto storyDetailDto = AdminStoryDetailDto.builder()
                .id(1L)
                .title("title")
                .content("content")
                .thumbnailImg("thumbnail")
                .latitude(-10.0)
                .longitude(10.0)
                .city("서울")
                .likeCnt(3L)
                .createDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .imgUrls(new ArrayList<>())
                .isDeleted(true)
                .build();

        given(adminStoryService.findStoryDetail(anyLong())).willReturn(storyDetailDto);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/stories/{story_id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.STORY_READ_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.STORY_READ_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.likeCnt").value(3L))
                .andExpect(jsonPath("$.data.city").value("서울"))
                .andExpect(jsonPath("$.data.isDeleted").value(true));

    }

    @Test
    @DisplayName("관리자 스토리 삭제 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    void deleteStory() throws Exception {

        //given

        //when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/admin/stories/{story_id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.STORY_DELETE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.STORY_DELETE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

    }
}