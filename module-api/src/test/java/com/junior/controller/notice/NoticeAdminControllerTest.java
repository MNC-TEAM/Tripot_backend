package com.junior.controller.notice;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.notice.CreateNoticeDto;
import com.junior.dto.notice.NoticeAdminDto;
import com.junior.dto.notice.NoticeDetailDto;
import com.junior.dto.notice.UpdateNoticeDto;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.security.WithMockCustomAdmin;
import com.junior.service.notice.NoticeAdminService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(NoticeAdminController.class)
class NoticeAdminControllerTest extends BaseControllerTest {

    @MockBean
    private NoticeAdminService noticeAdminService;

    @Test
    @DisplayName("공지 저장 - 응답이 반환되어야 함")
    @WithMockCustomAdmin
    void saveNotice() throws Exception {

        //given
        CreateNoticeDto createNoticeDto = new CreateNoticeDto("title", "content");
        String content = objectMapper.writeValueAsString(createNoticeDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/admin/notices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );


        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customCode").value(StatusCode.NOTICE_CREATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.NOTICE_CREATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));


    }

    @Test
    @DisplayName("공지 조회 - 응답이 반환되어야 함")
    @WithMockCustomAdmin
    void findNotice() throws Exception {

        //given
        Pageable resultPageable = PageRequest.of(0, 15);
        String q = "";

        List<NoticeAdminDto> result = new ArrayList<>();

        result.add(new NoticeAdminDto(1L, "title", LocalDateTime.of(2025, 1, 1, 0, 0)));


        given(noticeAdminService.findNotice(anyString(), any(Pageable.class))).willReturn(new PageCustom<>(result, resultPageable, result.size()));

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/notices")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.NOTICE_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.NOTICE_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("title"));


    }

    @Test
    @DisplayName("공지 세부내용 조회 - 응답이 반환되어야 함")
    @WithMockCustomAdmin
    void findNoticeDetail() throws Exception {

        //given

        NoticeDetailDto noticeDetailDto = NoticeDetailDto.builder()
                .id(1L)
                .title("title")
                .content("content")
                .build();

        given(noticeAdminService.findNoticeDetail(anyLong())).willReturn(noticeDetailDto);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/notices/{notice_id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.NOTICE_FIND_DETAIL_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.NOTICE_FIND_DETAIL_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.content").value("content"));


    }


    @Test
    @DisplayName("공지 수정 - 응답이 반환되어야 함")
    @WithMockCustomAdmin
    void updateNotice() throws Exception {

        //given
        Long noticeId = 1L;
        UpdateNoticeDto updateNoticeDto = new UpdateNoticeDto("new title", "new content");
        String content = objectMapper.writeValueAsString(updateNoticeDto);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/notices/{notice_id}", noticeId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.NOTICE_UPDATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.NOTICE_UPDATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("공지사항 삭제 - 응답이 반환되어야 함")
    @WithMockCustomAdmin
    void deleteNotice() throws Exception {

        //given
        Long noticeId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/admin/notices/{notice_id}", noticeId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.NOTICE_DELETE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.NOTICE_DELETE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}