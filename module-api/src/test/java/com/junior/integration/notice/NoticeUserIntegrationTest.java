package com.junior.integration.notice;

import com.junior.domain.admin.Notice;
import com.junior.domain.member.Member;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.notice.NoticeRepository;
import com.junior.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class NoticeUserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @BeforeEach
    void init() throws InterruptedException {
        Member preactiveTestMember = createPreactiveTestMember();
        Member activeTestMember = createActiveTestMember();
        Member testAdmin = createAdmin();
        Member activeTestMember2 = createActiveTestMember2();

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeTestMember);
        memberRepository.save(testAdmin);
        memberRepository.save(activeTestMember2);

        for (int i = 1; i <= 100; i++) {
            Notice notice = Notice.builder()
                    .title("공지사항 " + i)
                    .content("내용 " + i)
                    .member(testAdmin)
                    .isHtml(i % 2 != 0)         //홀수 공지면 true, 짝수 공지면 false
                    .build();

            noticeRepository.save(notice);

            Thread.sleep(3);

        }

    }

    @Test
    @DisplayName("사용자 공지 조회 - 공지 리스트가 정상적으로 반환되어야 함")
    @WithMockCustomUser
    void findNotice() throws Exception {

        //given

        Integer size = 5;


        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/notices")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("size", size.toString())
        );


        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("$.customCode").value(StatusCode.NOTICE_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.NOTICE_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.content[0].title").value("공지사항 100"))
                .andExpect(jsonPath("$.data.content[0].content").value("내용 100"))
                .andExpect(jsonPath("$.data.content[0].isHtml").value(false));


    }

    @Test
    @DisplayName("사용자 공지 조회 - 커서의 다음에 해당하는 공지부터 응답되어야 함")
    @WithMockCustomUser
    void findNoticeWithCursor() throws Exception {

        //given

        Long cursorId = 90L;
        Integer size = 5;


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
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.content[0].title").value("공지사항 89"))
                .andExpect(jsonPath("$.data.content[0].content").value("내용 89"))
                .andExpect(jsonPath("$.data.content[0].isHtml").value(true));


    }

    @Test
    @DisplayName("사용자 공지 조회 - 마지막 슬라이스에 해당하는 공지가 정상적으로 응답되어야 함")
    @WithMockCustomUser
    void findNoticeLast() throws Exception {

        //given

        Long cursorId = 3L;
        Integer size = 5;


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
                .andExpect(jsonPath("$.data.pageable.pageSize").value(5))
                .andExpect(jsonPath("$.data.last").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("공지사항 2"))
                .andExpect(jsonPath("$.data.content[0].content").value("내용 2"))
                .andExpect(jsonPath("$.data.content[0].isHtml").value(false));


    }
}
