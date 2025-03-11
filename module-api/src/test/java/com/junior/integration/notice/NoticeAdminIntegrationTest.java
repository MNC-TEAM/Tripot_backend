package com.junior.integration.notice;

import com.junior.domain.admin.Notice;
import com.junior.domain.member.Member;
import com.junior.dto.notice.CreateNoticeDto;
import com.junior.dto.notice.UpdateNoticeDto;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.notice.NoticeRepository;
import com.junior.security.WithMockCustomAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NoticeAdminIntegrationTest extends BaseIntegrationTest {

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
                    .build();

            noticeRepository.save(notice);

            Thread.sleep(3);

        }

    }


    @Test
    @DisplayName("공지 저장 - 새 공지사항이 정상적으로 저장되어야 함")
    @WithMockCustomAdmin
    void saveNotice() throws Exception {

        //given
        CreateNoticeDto createNoticeDto = new CreateNoticeDto("title 101", "content 101", true);
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

        Notice newNotice = noticeRepository.findById(101L)
                .orElseThrow(RuntimeException::new);

        assertThat(newNotice.getTitle()).isEqualTo("title 101");
        assertThat(newNotice.getContent()).isEqualTo("content 101");
        assertThat(newNotice.getIsHtml()).isTrue();


    }

    @Test
    @DisplayName("공지 조회 - 기능이 정상적으로 동작해야 함")
    @WithMockCustomAdmin
    void findNotice() throws Exception {

        //given


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
                .andExpect(jsonPath("$.data.content[0].title").value("공지사항 100"));


    }

    @Test
    @DisplayName("공지 조회 - 검색 기능이 정상적으로 동작해야 함")
    @WithMockCustomAdmin
    void findNoticeWithKeyword() throws Exception {

        //given


        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/notices")
                        .queryParam("q", "31")
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
                .andExpect(jsonPath("$.data.content[0].title").value("공지사항 31"));


    }

    @Test
    @DisplayName("공지 세부내용 조회 - 공지 새부내용을 정상적으로 응답해야 함")
    @WithMockCustomAdmin
    void findNoticeDetail() throws Exception {

        //given

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/notices/{notice_id}", 45L)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.NOTICE_FIND_DETAIL_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.NOTICE_FIND_DETAIL_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.title").value("공지사항 45"))
                .andExpect(jsonPath("$.data.content").value("내용 45"));


    }

    @Test
    @DisplayName("공지 수정 - 기능이 정상적으로 동작해야 함")
    @WithMockCustomAdmin
    void updateNotice() throws Exception {

        //given
        Long noticeId = 20L;
        UpdateNoticeDto updateNoticeDto = new UpdateNoticeDto("new title", "new content", true);
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

        Notice updatedNotice = noticeRepository.findById(noticeId)
                .orElseThrow(RuntimeException::new);

        assertThat(updatedNotice.getTitle()).isEqualTo("new title");
        assertThat(updatedNotice.getContent()).isEqualTo("new content");
        assertThat(updatedNotice.getIsHtml()).isTrue();

    }

    @Test
    @DisplayName("공지사항 삭제 - 공지의 삭제 flag가 true로 수정되어야 함")
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

        Notice deletedNotice = noticeRepository.findById(noticeId)
                .orElseThrow(RuntimeException::new);

        assertThat(deletedNotice.getIsDeleted()).isTrue();
    }

}
