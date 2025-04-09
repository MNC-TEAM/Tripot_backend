package com.junior.service.report;

import com.junior.domain.member.Member;
import com.junior.domain.report.Report;
import com.junior.domain.report.ReportReason;
import com.junior.domain.report.ReportStatus;
import com.junior.domain.report.ReportType;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import com.junior.dto.report.CreateReportDto;
import com.junior.dto.report.ReportDto;
import com.junior.dto.report.ReportQueryDto;
import com.junior.dto.report.StoryReportDto;
import com.junior.dto.story.AdminStoryDetailDto;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.ReportException;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.repository.comment.CommentRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.report.ReportRepository;
import com.junior.repository.story.StoryRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class ReportServiceTest extends BaseServiceTest {

    @Mock
    private StoryRepository storyRepository;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("신고 - 스토리 신고 기능이 정상 작동해야 함")
    void reportStory() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("story")
                .reportReason("스팸홍보")
                .build();

        Member testWriter = createActiveTestMember();
        Member testReporter = createActiveTestMember2();
        Story testStory = createStory(testWriter, "title", "city");

        UserPrincipal principal = new UserPrincipal(testReporter);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReporter));
        given(storyRepository.findById(anyLong())).willReturn(Optional.ofNullable(testStory));

        //when
        reportService.save(createReportDto, principal);

        //then

        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("신고 - 회원을 찾지 못했을 경우 예외 처리를 해야 함")
    void failToReportIfMemberNotFound() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("story")
                .reportReason("스팸홍보")
                .build();

        Member testWriter = createActiveTestMember();
        Member testReporter = createActiveTestMember2();

        UserPrincipal principal = new UserPrincipal(testReporter);

        //when, then
        assertThatThrownBy(() -> reportService.save(createReportDto, principal))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.INVALID_MEMBER.getCustomMessage());


    }

    @Test
    @DisplayName("신고 - 유효하지 않은 신고 유형에 대한 예외 처리가 진행되어야 함")
    void failToReportIfInvalidReportType() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("notvalid")
                .reportReason("스팸홍보")
                .build();

        Member testActiveMember = createActiveTestMember();


        UserPrincipal principal = new UserPrincipal(testActiveMember);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testActiveMember));


        //when, then
        assertThatThrownBy(() -> reportService.save(createReportDto, principal))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_NOT_VALID.getCustomMessage());
    }

    @Test
    @DisplayName("신고 - 신고 사유를 잘 못 적었을 경우 예외 처리를 해야 함")
    void failToReportIfInvalidReportReason() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("story")
                .reportReason("잘못된사유")
                .build();

        Member testWriter = createActiveTestMember();
        Member testReporter = createActiveTestMember2();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReporter));
        UserPrincipal principal = new UserPrincipal(testReporter);

        //when, then
        assertThatThrownBy(() -> reportService.save(createReportDto, principal))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_NOT_VALID.getCustomMessage());


    }

    @Test
    @DisplayName("신고 - 댓글 신고 기능이 정상 작동해야 함")
    void save_report_comment() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("comment")
                .reportReason("스팸홍보")
                .build();

        Member testWriter = createActiveTestMember();
        Member testReporter = createActiveTestMember2();
        Story testStory = createStory(testWriter, "title", "city");
        Comment testComment = createComment(testWriter, testStory);


        UserPrincipal principal = new UserPrincipal(testReporter);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReporter));
        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(testComment));


        //when
        reportService.save(createReportDto, principal);

        //then

        verify(reportRepository).save(any(Report.class));
    }

    @Test
    @DisplayName("신고 - 본인 스토리는 신고가 불가능해야 함")
    void failToReportIfAuthorReportsHisOwnStory() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("story")
                .reportReason("스팸홍보")
                .build();

        Member testWriter = createActiveTestMember();
        Member testWriter2 = createActiveTestMember();

        Story testStory = createStory(testWriter, "title", "city");
        Comment testComment = createComment(testWriter, testStory);


        UserPrincipal principal = new UserPrincipal(testWriter);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testWriter));
        given(storyRepository.findById(anyLong())).willReturn(Optional.ofNullable(testStory));


        //when, then
        assertThatThrownBy(() -> reportService.save(createReportDto, principal))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_EQUALS_AUTHOR.getCustomMessage());
    }

    @Test
    @DisplayName("신고 - 본인 댓글은 신고가 불가능해야 함")
    void failToReportIfAuthorReportsHisOwnComment() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("comment")
                .reportReason("스팸홍보")
                .build();

        Member testWriter = createActiveTestMember();
        Member testWriter2 = createActiveTestMember();

        Story testStory = createStory(testWriter, "title", "city");
        Comment testComment = createComment(testWriter, testStory);


        UserPrincipal principal = new UserPrincipal(testWriter);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testWriter));
        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(testComment));


        //when, then
        assertThatThrownBy(() -> reportService.save(createReportDto, principal))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_EQUALS_AUTHOR.getCustomMessage());
    }

    @Test
    @DisplayName("신고 - 스토리 중복신고가 불가능해야함")
    void failToReportIfReportStoryAgain() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("story")
                .reportReason("스팸홍보")
                .build();

        Member testWriter = createActiveTestMember();
        Member testReporter = createActiveTestMember2();
        Story testStory = createStory(testWriter, "title", "city");
        Comment testComment = createComment(testWriter, testStory);


        UserPrincipal principal = new UserPrincipal(testReporter);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReporter));
        given(storyRepository.findById(anyLong())).willReturn(Optional.ofNullable(testStory));
        given(reportRepository.existsByMemberAndStory(any(Member.class), any(Story.class))).willReturn(true);


        //when, then
        assertThatThrownBy(() -> reportService.save(createReportDto, principal))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_DUPLICATE.getCustomMessage());
    }

    @Test
    @DisplayName("신고 - 댓글 중복신고가 불가능해야함")
    void failToReportIfReportCommentAgain() {

        //given
        CreateReportDto createReportDto = CreateReportDto.builder()
                .reportContentId(1L)
                .reportType("comment")
                .reportReason("스팸홍보")
                .build();

        Member testWriter = createActiveTestMember();
        Member testReporter = createActiveTestMember2();
        Story testStory = createStory(testWriter, "title", "city");
        Comment testComment = createComment(testWriter, testStory);


        UserPrincipal principal = new UserPrincipal(testReporter);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReporter));
        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(testComment));
        given(reportRepository.existsByMemberAndComment(any(Member.class), any(Comment.class))).willReturn(true);


        //when, then
        assertThatThrownBy(() -> reportService.save(createReportDto, principal))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_DUPLICATE.getCustomMessage());
    }


    @Test
    @DisplayName("신고 조회 - 스토리 신고 내역 조회가 정상 동작해야 함")
    public void findStoryReport() throws Exception {
        //given

        String reportStatus = "CONFIRMED";

        //전체 신고내역 생성
        List<ReportQueryDto> queryDtoList = new ArrayList<>();
        List<ReportDto> dtoList = new ArrayList<>();


        queryDtoList.add(ReportQueryDto.builder()
                .id(1L)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET)
                .reporterUsername("username")
                .reportStatus(ReportStatus.CONFIRMED)
                .build());

        dtoList.add(StoryReportDto.builder()
                .id(1L)
                .reportType(ReportType.STORY)
                .reportReason(ReportReason.SPAMMARKET.getName())
                .reporterUsername("username")
                .reportStatus(ReportStatus.CONFIRMED)
                .build());


        Pageable requestPageable = PageRequest.of(1, 15);
        Pageable pageableAfterFind = PageRequest.of(0, 15);

        PageImpl<ReportQueryDto> pageList = new PageImpl<>(queryDtoList, pageableAfterFind, 0);

        given(reportRepository.findReport(any(ReportStatus.class), any(Pageable.class))).willReturn(pageList);

        //when
        PageCustom<ReportDto> report = reportService.findReport(reportStatus, requestPageable);

        //then
        assertThat(report.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(report.getContent().get(0).getReportType()).isEqualTo(ReportType.STORY);
        assertThat(report.getPageable().getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("신고 조회 - 댓글 신고 내역 조회가 정상 동작해야 함")
    public void findCommentReportReport() throws Exception {
        //given

        String reportStatus = "CONFIRMED";

        //전체 신고내역 생성
        List<ReportQueryDto> queryDtoList = new ArrayList<>();
        List<ReportDto> dtoList = new ArrayList<>();


        queryDtoList.add(ReportQueryDto.builder()
                .id(1L)
                .reportType(ReportType.COMMENT)
                .reportReason(ReportReason.SPAMMARKET)
                .reporterUsername("username")
                .reportStatus(ReportStatus.CONFIRMED)
                .build());

        dtoList.add(StoryReportDto.builder()
                .id(1L)
                .reportType(ReportType.COMMENT)
                .reportReason(ReportReason.SPAMMARKET.getName())
                .reporterUsername("username")
                .reportStatus(ReportStatus.CONFIRMED)
                .build());


        Pageable requestPageable = PageRequest.of(1, 15);
        Pageable pageableAfterFind = PageRequest.of(0, 15);

        PageImpl<ReportQueryDto> pageList = new PageImpl<>(queryDtoList, pageableAfterFind, 0);

        given(reportRepository.findReport(any(ReportStatus.class), any(Pageable.class))).willReturn(pageList);

        //when
        PageCustom<ReportDto> report = reportService.findReport(reportStatus, requestPageable);

        //then
        assertThat(report.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(report.getContent().get(0).getReportType()).isEqualTo(ReportType.COMMENT);
        assertThat(report.getPageable().getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("신고 조회 - 신고 상태가 적절하지 않을 경우 예외 처리를 해야 함")
    public void failToFindReportIfReportStatusIsNotValid() throws Exception {
        //given
        String reportStatus = "ALL2";

        PageRequest pageRequest = PageRequest.of(0, 15);

        //when  //then
        assertThatThrownBy(() -> reportService.findReport(reportStatus, pageRequest))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_TYPE_NOT_VALID.getCustomMessage());

    }

    @Test
    @DisplayName("신고 대상 스토리 상세 조회 - 스토리 상세 조회 기능이 정상 동작해야 함")
    void findReportTargetStoryDetail() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory);

        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));

        //when
        AdminStoryDetailDto result = reportService.findReportDetail(1L);

        //then
        assertThat(result.city()).isEqualTo("city");
        assertThat(result.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("신고 대상 스토리 상세 조회 - 댓글 신고 내역에 대해 예외를 발생시켜야 함")
    void failToFindReportTargetStoryDetailIfReportTypeIsComment() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");
        Comment testComment = createComment(testActiveMember, testStory);
        Report testReport = createReport(testActiveMember, ReportType.COMMENT, testComment);

        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));

        //when
        assertThatThrownBy(() -> reportService.findReportDetail(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_NOT_VALID.getCustomMessage());
    }

    @Test
    @DisplayName("신고 확인 - 신고 상태가 확인 상태로 변경되어야 함")
    void confirmReport() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when
        reportService.confirmReport(1L);

        //then
        Report report = reportRepository.findById(1L).orElseThrow(() -> new RuntimeException());

        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.CONFIRMED);


    }

    @Test
    @DisplayName("신고 확인 - 확인할 신고를 찾지 못할 시 예외 처리를 해야 함")
    void failToConfirmReportIfReportNotFound() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");


        //when, then
        assertThatThrownBy(() -> reportService.confirmReport(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_NOT_FOUND.getCustomMessage());


    }

    @Test
    @DisplayName("신고 처리 - 이미 처리된 신고일 경우 예외 처리를 해야 함")
    void failToConfirmTargetIfReportAlreadyConfirmed() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory, ReportStatus.CONFIRMED);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when, then
        assertThatThrownBy(() -> reportService.confirmReport(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_ALREADY_CONFIRMED.getCustomMessage());


    }

    @Test
    @DisplayName("신고 확인 - 이미 삭제 처리된 신고일 경우 예외 처리를 해야 함")
    void failToConfirmReportIfReportAlreadyTargetDeleted() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory, ReportStatus.DELETED);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when, then
        assertThatThrownBy(() -> reportService.confirmReport(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomMessage());


    }

    @Test
    @DisplayName("신고 확인 - 이미 삭제된 스토리를 처리하려 할 경우 예외 처리를 해야 함")
    void failToConfirmReportIfReportTargetStoryAlreadyDeleted() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");
        testStory.deleteStory();

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when, then
        assertThatThrownBy(() -> reportService.confirmReport(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomMessage());


    }

    @Test
    @DisplayName("신고 확인 - 이미 삭제된 댓글을 처리하려 할 경우 예외 처리를 해야 함")
    void failToConfirmReportIfReportTargetCommentAlreadyDeleted() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");
        Comment testComment = createComment(testActiveMember, testStory);

        testComment.deleteComment();

        Report testReport = createReport(testActiveMember, ReportType.COMMENT, testComment);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when, then
        assertThatThrownBy(() -> reportService.confirmReport(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomMessage());


    }

    @Test
    @DisplayName("신고 대상 삭제 - 신고당한 스토리에 대한 삭제 처리가 정상적으로 이루어져야 함")
    void deleteStoryReport() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory);

        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));

        //when
        reportService.deleteReportTarget(1L);

        //then
        Report resultReport = reportRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.DELETED);
        assertThat(resultReport.getStory().getIsDeleted()).isTrue();

    }

    @Test
    @DisplayName("신고 대상 삭제 - 신고당한 댓글에 대한 삭제 처리가 정상적으로 이루어져야 함")
    void deleteCommentReport() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");
        Comment testComment = createComment(testActiveMember, testStory);

        Report testReport = createReport(testActiveMember, ReportType.COMMENT, testComment);

        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));

        //when
        reportService.deleteReportTarget(1L);

        //then
        Report resultReport = reportRepository.findById(1L).orElseThrow(RuntimeException::new);

        assertThat(resultReport.getReportStatus()).isEqualTo(ReportStatus.DELETED);
        assertThat(resultReport.getComment().getIsDeleted()).isTrue();

    }

    @Test
    @DisplayName("신고 대상 삭제 - 이미 처리된 신고일 경우 예외 처리를 해야 함")
    void failToDeleteReportTargetIfReportAlreadyConfirmed() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory, ReportStatus.CONFIRMED);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when, then
        assertThatThrownBy(() -> reportService.deleteReportTarget(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_ALREADY_CONFIRMED.getCustomMessage());


    }

    @Test
    @DisplayName("신고 대상 삭제 - 이미 삭제 처리된 신고일 경우 예외 처리를 해야 함")
    void failToDeleteReportTargetIfReportAlreadyTargetDeleted() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory, ReportStatus.DELETED);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when, then
        assertThatThrownBy(() -> reportService.deleteReportTarget(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomMessage());


    }

    @Test
    @DisplayName("신고 대상 삭제 - 이미 삭제된 스토리를 처리하려 할 경우 예외 처리를 해야 함")
    void failToDeleteReportTargetIfReportTargetStoryAlreadyDeleted() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");
        testStory.deleteStory();

        Report testReport = createReport(testActiveMember, ReportType.STORY, testStory);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when, then
        assertThatThrownBy(() -> reportService.deleteReportTarget(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomMessage());


    }

    @Test
    @DisplayName("신고 확인 - 이미 삭제된 댓글을 처리하려 할 경우 예외 처리를 해야 함")
    void failToDeleteReportTargetIfReportTargetCommentAlreadyDeleted() {

        //given
        Member testActiveMember = createActiveTestMember();
        Story testStory = createStory(testActiveMember, "title", "city");
        Comment testComment = createComment(testActiveMember, testStory);

        testComment.deleteComment();

        Report testReport = createReport(testActiveMember, ReportType.COMMENT, testComment);


        given(reportRepository.findById(anyLong())).willReturn(Optional.ofNullable(testReport));


        //when, then
        assertThatThrownBy(() -> reportService.deleteReportTarget(1L))
                .isInstanceOf(ReportException.class)
                .hasMessageContaining(StatusCode.REPORT_TARGET_ALREADY_DELETED.getCustomMessage());


    }


}