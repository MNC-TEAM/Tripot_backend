package com.junior.controller.notice;

import com.junior.controller.BaseControllerTest;
import com.junior.dto.notice.NoticeUserDto;
import com.junior.exception.StatusCode;
import com.junior.security.WithMockCustomUser;
import com.junior.service.notice.NoticeUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(NoticeUserController.class)
class NoticeUserControllerTest extends BaseControllerTest {

    @MockBean
    private NoticeUserService noticeUserService;

    @Test
    @DisplayName("사용자 공지 조회 - 응답이 반환되어야 함")
    @WithMockCustomUser
    void findNotice() throws Exception {

        //given

        Long cursorId = 1L;
        Integer size = 5;

        PageRequest pageable = PageRequest.of(0, size);

        List<NoticeUserDto> result = new ArrayList<>();

        result.add(new NoticeUserDto(1L, "title", "content", LocalDateTime.MIN, true));

        given(noticeUserService.findNotice(cursorId, size)).willReturn(new SliceImpl<>(result, pageable, false));

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/notices")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("cursorId", cursorId.toString())
                        .param("size", size.toString())
        );


        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("$.customCode").value(StatusCode.NOTICE_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.NOTICE_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.content[0].title").value("title"));


    }
}