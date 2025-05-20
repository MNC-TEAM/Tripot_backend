package com.junior.controller.comment;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.comment.CommentAdminDto;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.security.WithMockCustomAdmin;
import com.junior.service.comment.CommentAdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentAdminController.class)
class CommentAdminControllerTest extends BaseControllerTest {

    @MockBean
    private CommentAdminService commentAdminService;

    @InjectMocks
    private CommentAdminController commentAdminController;

    @Test
    @DisplayName("관리자 댓글 조회 - 정상적으로 응답을 반환해야 함")
    @WithMockCustomAdmin
    void findComment() throws Exception {

        //given
        PageRequest pageRequest = PageRequest.of(0, 15);

        List<CommentAdminDto> commentAdminDtos = new ArrayList<>();

        CommentAdminDto commentAdminDto = CommentAdminDto.builder()
                .id(1L)
                .content("test comment content")
                .createdNickname("nickname")
                .isDeleted(false)
                .build();

        commentAdminDtos.add(commentAdminDto);

        given(commentAdminService.findComment(any(Pageable.class))).willReturn(new PageCustom<>(commentAdminDtos, pageRequest, 1L));

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/comments")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.COMMENT_READ_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.COMMENT_READ_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1))
                .andExpect(jsonPath("$.data.content[0].createdNickname").value("nickname"));

    }

    @Test
    @DisplayName("관리자용 댓글 삭제 - 정상적으로 응답을 반환해야 함")
    @WithMockCustomAdmin
    void deleteComment() throws Exception {

        //given


        //when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/admin/comments/{comment_id}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.COMMENT_DELETE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.COMMENT_DELETE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

    }
}