package com.junior.service.notice;

import com.junior.domain.admin.Notice;
import com.junior.domain.member.Member;
import com.junior.dto.notice.CreateNoticeDto;
import com.junior.dto.notice.NoticeAdminDto;
import com.junior.dto.notice.NoticeDetailDto;
import com.junior.dto.notice.UpdateNoticeDto;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.NoticeException;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.notice.NoticeRepository;
import com.junior.security.UserPrincipal;
import com.junior.service.BaseServiceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class NoticeAdminServiceTest extends BaseServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @InjectMocks
    private NoticeAdminService noticeAdminService;


    @Test
    @DisplayName("공지사항 저장 - 정상적으로 실행되어야 함")
    void saveNotice() {

        //given
        CreateNoticeDto createNoticeDto = new CreateNoticeDto("title", "content");
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(testMember));

        //when
        noticeAdminService.saveNotice(principal, createNoticeDto);

        //then
        verify(noticeRepository).save(any(Notice.class));


    }

    @Test
    @DisplayName("공지사항 저장 - 회원을 찾지 못했을 때 관련 예외처리를 해야 함")
    void failToSaveNoticeIfMemberNotFound() {

        //given
        CreateNoticeDto createNoticeDto = new CreateNoticeDto("title", "content");
        Member testMember = createActiveTestMember();
        UserPrincipal principal = new UserPrincipal(testMember);

        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> noticeAdminService.saveNotice(principal, createNoticeDto))
                .isInstanceOf(NotValidMemberException.class)
                .withFailMessage("유효하지 않은 회원");


    }

    @Test
    @DisplayName("공지사항 관리자 조회 - 정상적으로 실행되어야 함")
    void findNotice() {

        //given

        //전체 공지 생성
        List<Notice> entityList = new ArrayList<>();

        entityList.add(createNotice());

        List<NoticeAdminDto> dtoList = new ArrayList<>();

        dtoList.add(new NoticeAdminDto(1L, "title", LocalDateTime.of(2025, 1, 1, 0, 0)));

        String q = "";
        Pageable requestPageable = PageRequest.of(1, 15);
        Pageable pageableAfterFind = PageRequest.of(0, 15);

        PageImpl<NoticeAdminDto> pageList = new PageImpl<>(dtoList, pageableAfterFind, 0);

        given(noticeRepository.findNotice(anyString(), any(Pageable.class))).willReturn(pageList);

        //when

        PageCustom<NoticeAdminDto> notice = noticeAdminService.findNotice(q, requestPageable);

        //then

        //공지사항이 정상적으로 리턴되었는지 검증
        assertThat(notice.getContent().get(0).id()).isEqualTo(1L);
        assertThat(notice.getContent().get(0).title()).isEqualTo("title");

        //1-indexed 페이징이 정상적으로 동작해야 함
        assertThat(notice.getPageable().getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("공지사항 세부 조회 - 정상적으로 실행되어야 함")
    void findNoticeDetail() {

        //given

        Long noticeId = 1L;
        Notice notice = createNotice();


        given(noticeRepository.findById(1L)).willReturn(Optional.ofNullable(notice));
        given(memberRepository.findById(notice.getCreatedBy())).willReturn(Optional.ofNullable(Member.builder()
                .id(1L)
                .nickname("nickname")
                .build()));


        //when
        NoticeDetailDto noticeDetail = noticeAdminService.findNoticeDetail(noticeId);

        //then
        assertThat(noticeDetail.id()).isEqualTo(1L);
        assertThat(noticeDetail.title()).isEqualTo("title");
        assertThat(noticeDetail.content()).isEqualTo("content");


    }

    @Test
    @DisplayName("공지사항 세부 조회 - 공지사항을 찾지 못했을 때 관련 예외처리를 해야 함")
    void failToFindNoticeDetailIfNoticeNotFound() {


        //given

        Long noticeId = 1L;
        Notice notice = createNotice();


        given(noticeRepository.findById(1L)).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> noticeAdminService.findNoticeDetail(noticeId))
                .isInstanceOf(NoticeException.class)
                .hasMessageContaining(StatusCode.NOTICE_NOT_FOUND.getCustomMessage());

    }

    @Test
    @DisplayName("공지사항 세부 조회 - 삭제된 공지사항에 대해 관련 예외 처리를 해야 함")
    void failToFindNoticeDetailIfFindDeletedNotice() {

        //given

        Long noticeId = 1L;
        Notice notice = createNotice();
        notice.softDelete();

        given(noticeRepository.findById(1L)).willReturn(Optional.ofNullable(notice));

        //when, then
        assertThatThrownBy(() -> noticeAdminService.findNoticeDetail(noticeId))
                .isInstanceOf(NoticeException.class)
                .hasMessageContaining(StatusCode.NOTICE_NOT_FOUND.getCustomMessage());

    }

    @Test
    @DisplayName("공지사항 세부 조회 - 찾을 수 없는 회원에 대해 관련 예외처리를 해야 함")
    void failToFindNoticeDetailIfMemberNotFound() {

        //given

        Long noticeId = 1L;
        Notice notice = createNotice();


        given(noticeRepository.findById(1L)).willReturn(Optional.ofNullable(notice));
        given(memberRepository.findById(notice.getCreatedBy())).willReturn(Optional.empty());


        //when, then
        assertThatThrownBy(() -> noticeAdminService.findNoticeDetail(noticeId))
                .isInstanceOf(NotValidMemberException.class)
                .hasMessageContaining(StatusCode.MEMBER_NOT_FOUND.getCustomMessage());

    }

    @Test
    @DisplayName("공지사항 수정 - 정상적으로 실행되어야 함")
    void updateNotice() {

        //given
        Long updateNoticeId = 1L;
        UpdateNoticeDto updateNoticeDto = new UpdateNoticeDto("new title", "new content");

        Notice notice = createNotice();

        given(noticeRepository.findById(updateNoticeId)).willReturn(Optional.ofNullable(notice));

        //when
        noticeAdminService.updateNotice(updateNoticeId, updateNoticeDto);

        //then
        Notice updatedNotice = noticeRepository.findById(1L).get();

        assertThat(notice.getTitle()).isEqualTo("new title");
        assertThat(notice.getContent()).isEqualTo("new content");


    }

    @Test
    @DisplayName("공지 수정 - 공지사항을 찾지 못했을 때 관련 예외처리를 해야 함")
    void failToUpdateNoticeIfNoticeNotFound() {

        //given
        Long updateNoticeId = 1L;
        UpdateNoticeDto updateNoticeDto = new UpdateNoticeDto("new title", "new content");


        //when, then
        Assertions.assertThatThrownBy(() -> noticeAdminService.updateNotice(updateNoticeId, updateNoticeDto))
                .isInstanceOf(NoticeException.class)
                .hasMessageContaining(StatusCode.NOTICE_NOT_FOUND.getCustomMessage());


    }

    @Test
    @DisplayName("공지사항 삭제 - 정상적으로 실행되어야 함")
    void deleteNotice() {

        //given
        Notice notice = createNotice();
        Long deleteNoticeId = 1L;

        given(noticeRepository.findById(deleteNoticeId)).willReturn(Optional.ofNullable(notice));

        //when
        noticeAdminService.deleteNotice(deleteNoticeId);

        //then
        assertThat(notice.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("공지 삭제 - 공지사항을 찾지 못했을 때 관련 예외처리를 해야 함")
    void failToDeleteNoticeIfNoticeNotFound() {

        //given
        Long deleteNoticeId = 1L;


        //when, then
        Assertions.assertThatThrownBy(() -> noticeAdminService.deleteNotice(deleteNoticeId))
                .isInstanceOf(NoticeException.class)
                .hasMessageContaining(StatusCode.NOTICE_NOT_FOUND.getCustomMessage());


    }

}