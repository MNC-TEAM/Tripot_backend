package com.junior.service.popUpEvent;

import com.junior.domain.member.Member;
import com.junior.domain.popUpEvent.PopUpEvent;
import com.junior.domain.popUpEvent.PopUpEventLike;
import com.junior.dto.popUpEvent.ResponsePopUpEventDto;
import com.junior.exception.CustomException;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.StatusCode;
import com.junior.repository.member.MemberRepository;
import com.junior.repository.popUpEvent.PopUpEventLikeRepository;
import com.junior.repository.popUpEvent.PopUpEventRepository;
import com.junior.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PopUpEventLikeService {

    private final MemberRepository memberRepository;
    private final PopUpEventRepository popUpEventRepository;
    private final PopUpEventLikeRepository popUpEventLikeRepository;

    @Transactional
    public void save(UserPrincipal principal, Long popUpEventId) {
        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        PopUpEvent popUpEvent = popUpEventRepository.findById(popUpEventId)
                .orElseThrow(() -> new CustomException(StatusCode.POPUPEVENT_READ_FAIL));

        boolean isLikePopUpEvent = popUpEventLikeRepository.existsByMemberAndPopUpEvent(member, popUpEvent);

        if (isLikePopUpEvent) {
            PopUpEventLike popUpEventLike = popUpEventLikeRepository.findByMemberAndPopUpEvent(member, popUpEvent)
                    .orElseThrow(() -> new CustomException(StatusCode.POPUPEVENT_LIKE_NOT_FOUND));

            log.info("[{}] 축제 북마크 삭제 member: {}, festival: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername(), popUpEvent.getEventName());
            popUpEventLikeRepository.delete(popUpEventLike);
        }

        PopUpEventLike popUpEventLike = PopUpEventLike.builder()
                .member(member)
                .popUpEvent(popUpEvent)
                .build();

        log.info("[{}] 팝업스토어 북마크 저장 member: {}, festival: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername(), popUpEvent.getEventName());

        popUpEventLikeRepository.save(popUpEventLike);
    }

    public Slice<ResponsePopUpEventDto> findPopUpEventsByLike(UserPrincipal principal, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER));

        log.info("[{}] 팝업스토어 리스트 조회", Thread.currentThread().getStackTrace()[1].getMethodName());

        return popUpEventLikeRepository.findPopUpEventByScroll(pageable, cursorId, member);
    }

    public void delete(UserPrincipal principal, Long popUpEventId) {
        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER));

        PopUpEvent popUpEvent = popUpEventRepository.findById(popUpEventId)
                .orElseThrow(() -> new CustomException(StatusCode.POPUPEVENT_READ_FAIL));

        PopUpEventLike popUpEventLike = popUpEventLikeRepository.findByMemberAndPopUpEvent(member, popUpEvent)
                .orElseThrow(() -> new CustomException(StatusCode.POPUPEVENT_LIKE_NOT_FOUND));

        log.info("[{}] 축제 북마크 삭제 member: {}, festival: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername(), popUpEvent.getEventName());
        popUpEventLikeRepository.delete(popUpEventLike);
    }
}
