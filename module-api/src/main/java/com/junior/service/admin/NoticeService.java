package com.junior.service.admin;

import com.junior.domain.admin.Notice;
import com.junior.domain.member.Member;
import com.junior.dto.admin.notice.CreateNoticeDto;
import com.junior.dto.admin.notice.NoticeDetailDto;
import com.junior.dto.admin.notice.NoticeDto;
import com.junior.dto.admin.notice.UpdateNoticeDto;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.NoticeException;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.page.PageableCustom;
import com.junior.repository.admin.NoticeRepository;
import com.junior.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final MemberRepository memberRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public void saveNotice(CreateNoticeDto createNoticeDto) {

        Notice notice = Notice.builder()
                .title(createNoticeDto.title())
                .content(createNoticeDto.content())
                .build();

        log.info("[{}] 공지사항 저장", Thread.currentThread().getStackTrace()[1].getMethodName());
        noticeRepository.save(notice);

    }


    public PageCustom<NoticeDto> findNotice(String q, Pageable pageable) {

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());

        Page<NoticeDto> page = noticeRepository.findNotice(q, pageRequest);

        log.info("[{}] 공지사항 조회 결과 리턴 page: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), pageable.getPageNumber());
        return new PageCustom<>(page.getContent(), page.getPageable(), page.getTotalElements());

    }

    public NoticeDetailDto findNoticeDetail(Long noticeId) {

        log.info("[{}] 공지사항 세부정보 조회", Thread.currentThread().getStackTrace()[1].getMethodName());
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(StatusCode.NOTICE_NOT_FOUND));

        if (notice.getIsDeleted()) {
            log.error("[{}] 삭제된 공지사항 조회 - id: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), notice.getId());
            throw new NoticeException(StatusCode.NOTICE_NOT_FOUND);
        }

        log.info("[{}] 공지사항 작성자 조회", Thread.currentThread().getStackTrace()[1].getMethodName());
        Member author = memberRepository.findById(notice.getCreatedBy())
                .orElseThrow(() -> new NotValidMemberException(StatusCode.MEMBER_NOT_FOUND));

        NoticeDetailDto noticeDetailDto = NoticeDetailDto.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .authorNick(author.getNickname()).build();

        return noticeDetailDto;

    }


    @Transactional
    public void updateNotice(Long updateNoticeId, UpdateNoticeDto updateNoticeDto) {

        Notice notice = noticeRepository.findById(updateNoticeId)
                .orElseThrow(() -> new NoticeException(StatusCode.NOTICE_NOT_FOUND));

        log.info("[{}] 공지사항 수정 내용 - id: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), updateNoticeId);
        notice.update(updateNoticeDto);


    }



    @Transactional
    public void deleteNotice(Long noticeId) {

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(StatusCode.NOTICE_NOT_FOUND));

        log.info("[{}] 공지사항 삭제 - id: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), noticeId);
        notice.softDelete();


    }
}
