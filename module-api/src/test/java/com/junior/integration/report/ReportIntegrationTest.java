package com.junior.integration.report;

import com.junior.controller.report.ReportController;
import com.junior.domain.member.Member;
import com.junior.domain.report.Report;
import com.junior.domain.report.ReportReason;
import com.junior.domain.report.ReportStatus;
import com.junior.domain.report.ReportType;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import com.junior.dto.report.CreateReportDto;
import com.junior.dto.story.AdminStoryDetailDto;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.comment.CommentRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.report.ReportRepository;
import com.junior.repository.story.StoryRepository;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser;
import com.junior.security.WithMockCustomUser2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ReportIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ReportController reportController;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReportRepository reportRepository;


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

        Story testStory = createStory(activeTestMember);
        storyRepository.save(testStory);

        Comment testComment = createComment(activeTestMember, testStory);
        commentRepository.save(testComment);

        for (int i = 1; i <= 100; i++) {
            Report testReport;

            if (i % 2 == 1) {

                testReport = Report.builder()
                        .reportType(ReportType.STORY)
                        .story(testStory)
                        .reportReason(ReportReason.SPAMMARKET)
                        .member(activeTestMember)
                        .build();


            } else {
                testReport = Report.builder()
                        .reportType(ReportType.COMMENT)
                        .comment(testComment)
                        .reportReason(ReportReason.SPAMMARKET)
                        .reportStatus(ReportStatus.CONFIRMED)
                        .member(activeTestMember)
                        .build();


            }

            Thread.sleep(5);

            reportRepository.save(testReport);
        }


    }

    @Test
    @DisplayName("신고 - 스토리에 대한 신고 기능이 정상적으로 이루어져야 함")
    @WithMockCustomUser2
    public void reportStory() throws Exception {
        //given
        CreateReportDto createReportDto = new CreateReportDto(1L, "STORY", "스팸홍보");
        String content = objectMapper.writeValueAsString(createReportDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/reports")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );


        //then
        actions
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_CREATE_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_CREATE_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        //신고 내역이 정상적으로 저장되어야 함
        Report report = reportRepository.findById(101L).orElseThrow(RuntimeException::new);

        assertThat(report.getMember().getUsername()).isEqualTo("테스트사용자유저네임2");
        assertThat(report.getReportType()).isEqualTo(ReportType.STORY);
        assertThat(report.getReportReason()).isEqualTo(ReportReason.SPAMMARKET);
        assertThat(report.getStory().getTitle()).isEqualTo("testStoryTitle");
        assertThat(report.getComment()).isNull();
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.UNCONFIRMED);


    }

    @Test
    @DisplayName("신고 - 본인 글은 신고할 수 없어야 함")
    @WithMockCustomUser
    public void failToReportStoryIfReporterEqualsAuthor() throws Exception {
        //given
        CreateReportDto createReportDto = new CreateReportDto(1L, "STORY", "스팸홍보");
        String content = objectMapper.writeValueAsString(createReportDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/reports")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );


        //then
        actions
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_EQUALS_AUTHOR.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_EQUALS_AUTHOR.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));


    }


    @Test
    @DisplayName("신고 - 중복 신고가 불가능해야 함")
    @WithMockCustomUser2
    public void failToReportStoryIfReportSameThing() throws Exception {
        //given
        CreateReportDto createReportDto = new CreateReportDto(1L, "STORY", "스팸홍보");
        String content = objectMapper.writeValueAsString(createReportDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/reports")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        ResultActions actions2 = mockMvc.perform(
                post("/api/v1/reports")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );


        //then
        actions2
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_DUPLICATE.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_DUPLICATE.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));


    }

    @Test
    @DisplayName("신고 조회 - 정상적으로 작동되어야 함")
    @WithMockCustomAdmin
    void findReport() throws Exception {

        //given
        String reportStatus = "UNCONFIRMED";

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/reports")
                        .queryParam("report_status", reportStatus)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1))
                .andExpect(jsonPath("$.data.content[0].reportReason").value("스팸홍보"))
                .andExpect(jsonPath("$.data.content[0].reportStatus").value("UNCONFIRMED"))
                .andExpect(jsonPath("$.data.content[0].storyId").value(1));


    }

    @Test
    @DisplayName("신고 대상 스토리 세부정보 조회 - 정상적으로 적동되어야 함")
    @WithMockCustomAdmin
    void findReportTargetStoryDetail() throws Exception {

        //given
        Long reportId = 1L;

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/reports/{report_id}/stories", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_FIND_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_FIND_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.title").value("testStoryTitle"))
                .andExpect(jsonPath("$.data.city").value("city"))
                .andExpect(jsonPath("$.data.isDeleted").value(false));

        Report resultReport = reportRepository.findById(reportId).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.CONFIRMED);
    }

    @Test
    @DisplayName("신고 대상 스토리 세부정보 조회 - 댓글 신고내역으로 요청이 들어올 경우 예외를 발생시켜야 함")
    @WithMockCustomAdmin
    void failToFindReportTargetStoryDetailIfReportTypeIsComment() throws Exception {

        //given
        Long reportId = 2L;

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/reports/{report_id}/stories", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_NOT_VALID.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_NOT_VALID.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Report resultReport = reportRepository.findById(reportId).orElseThrow(RuntimeException::new);

    }

    @Test
    @DisplayName("신고 확인 - 정상적으로 적동되어야 함")
    @WithMockCustomAdmin
    void confirmReport() throws Exception {

        //given
        Long reportId = 101L;

        Member testMember = memberRepository.findById(2L).get();
        Story testStory = storyRepository.findById(1L).get();

        Report report = Report.builder()
                .member(testMember)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET)
                .story(testStory)
                .build();

        reportRepository.save(report);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/confirm", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_CONFIRM_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_CONFIRM_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Report resultReport = reportRepository.findById(reportId).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.CONFIRMED);
    }

    @Test
    @DisplayName("신고 확인 - 이미 처리된 스토리일 경우 예외를 발생시켜야 함")
    @WithMockCustomAdmin
    void failToConfirmReportIfReportAlreadyConfirmed() throws Exception {

        //given
        Long reportId = 101L;

        Member testMember = memberRepository.findById(2L).get();
        Story testStory = storyRepository.findById(1L).get();

        Report report = Report.builder()
                .member(testMember)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET)
                .story(testStory)
                .build();

        report.confirmReport();

        reportRepository.save(report);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/confirm", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_ALREADY_CONFIRMED.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_ALREADY_CONFIRMED.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Report resultReport = reportRepository.findById(reportId).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.CONFIRMED);
    }

    @Test
    @DisplayName("신고 확인 - 이미 삭제된 스토리를 처리할 경우 예외를 발생시켜야 함")
    @WithMockCustomAdmin
    void failToConfirmReportIfReportTargetStoryAlreadyDeleted() throws Exception {

        //given
        Long reportId = 101L;

        Member testMember = memberRepository.findById(2L).get();
        Story testStory = storyRepository.findById(1L).get();

        testStory.deleteStory();

        Report report = Report.builder()
                .member(testMember)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET)
                .story(testStory)
                .build();

        reportRepository.save(report);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/confirm", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Report resultReport = reportRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.UNCONFIRMED);
    }

    @Test
    @DisplayName("신고 대상 삭제 - 스토리가 정상적으로 삭제되어야 함")
    @WithMockCustomAdmin
    void deleteReportTargetStory() throws Exception {

        //given
        Long reportId = 101L;

        Member testMember = memberRepository.findById(2L).get();
        Story testStory = storyRepository.findById(1L).get();

        Report report = Report.builder()
                .member(testMember)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET)
                .story(testStory)
                .build();

        reportRepository.save(report);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/delete-target", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_DELETE_TARGET_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_DELETE_TARGET_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Report resultReport = reportRepository.findById(101L).orElseThrow(RuntimeException::new);
        Story resultStory = storyRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.DELETED);
        assertThat(resultStory.getIsDeleted()).isTrue();

    }

    @Test
    @DisplayName("신고 대상 삭제 - 댓글이 정상적으로 삭제되어야 함")
    @WithMockCustomAdmin
    void deleteReportTargetComment() throws Exception {

        //given
        Long reportId = 101L;

        Member testMember = memberRepository.findById(2L).get();
        Comment testComment = commentRepository.findById(1L).get();


        Report report = Report.builder()
                .member(testMember)
                .reportType(ReportType.COMMENT)
                .reportReason(ReportReason.SPAMMARKET)
                .comment(testComment)
                .build();

        reportRepository.save(report);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/delete-target", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_DELETE_TARGET_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_DELETE_TARGET_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Report resultReport = reportRepository.findById(101L).orElseThrow(RuntimeException::new);
        Comment resultComment = commentRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.DELETED);
        assertThat(resultComment.getIsDeleted()).isTrue();

    }

    @Test
    @DisplayName("신고 대상 삭제 - 이미 처리된 스토리일 경우 예외를 발생시켜야 함")
    @WithMockCustomAdmin
    void failToDeleteReportTargetIfReportAlreadyConfirmed() throws Exception {

        //given
        Long reportId = 101L;

        Member testMember = memberRepository.findById(2L).get();
        Story testStory = storyRepository.findById(1L).get();

        Report report = Report.builder()
                .member(testMember)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET)
                .story(testStory)
                .build();

        report.confirmReport();

        reportRepository.save(report);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/delete-target", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_ALREADY_CONFIRMED.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_ALREADY_CONFIRMED.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Report resultReport = reportRepository.findById(reportId).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.CONFIRMED);
    }

    @Test
    @DisplayName("신고 확인 - 이미 삭제된 스토리를 처리할 경우 예외를 발생시켜야 함")
    @WithMockCustomAdmin
    void failToDeleteReportTargetIfReportTargetStoryAlreadyDeleted() throws Exception {

        //given
        Long reportId = 101L;

        Member testMember = memberRepository.findById(2L).get();
        Story testStory = storyRepository.findById(1L).get();

        testStory.deleteStory();

        Report report = Report.builder()
                .member(testMember)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET)
                .story(testStory)
                .build();

        reportRepository.save(report);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/admin/reports/{report_id}/delete-target", reportId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customCode").value(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.data").value(nullValue()));

        Report resultReport = reportRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.UNCONFIRMED);
    }



}
