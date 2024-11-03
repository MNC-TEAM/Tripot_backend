package com.junior.service;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberStatus;
import com.junior.dto.member.ActivateMemberDto;
import com.junior.dto.member.UpdateNicknameDto;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.StatusCode;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void activateMember(UserPrincipal principal, ActivateMemberDto activateMemberDto) {

        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );


        if (member.getStatus() != MemberStatus.PREACTIVE) {
            log.warn("[{}] Invalid member = {} member.status = {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername(), member.getStatus());
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER);
        }

        log.info("[{}}] target: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername());
        member.activateMember(activateMemberDto);

    }

    public Boolean checkDuplicateNickname(String nickname) {
        log.info("[checkDuplicateNickname] target nickname: {}", nickname);
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public void deleteMember(UserPrincipal principal) {

        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        if (member.getStatus() != MemberStatus.ACTIVE) {
            log.warn("[{}] Invalid member = {} member.status = {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername(), member.getStatus());
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER);
        }

        log.info("[{}] target: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername());
        member.deleteMember();

    }

    @Transactional
    public void updateNickname(UserPrincipal principal, UpdateNicknameDto updateNicknameDto) {

        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        if (member.getStatus() != MemberStatus.ACTIVE) {
            log.warn("[{}] Invalid member = {} member.status = {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername(), member.getStatus());
            throw new NotValidMemberException(StatusCode.INVALID_MEMBER);
        }

        member.updateNickname(updateNicknameDto);
    }
}
