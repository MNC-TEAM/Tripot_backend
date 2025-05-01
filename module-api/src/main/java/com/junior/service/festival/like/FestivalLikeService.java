package com.junior.service.festival.like;

import com.junior.domain.festival.Festival;
import com.junior.domain.festival.like.FestivalLike;
import com.junior.domain.member.Member;
import com.junior.exception.CustomException;
import com.junior.exception.NotValidMemberException;
import com.junior.exception.StatusCode;
import com.junior.repository.festival.FestivalLikeRepository;
import com.junior.repository.festival.FestivalRepository;
import com.junior.repository.member.MemberRepository;
import com.junior.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FestivalLikeService {

    private final MemberRepository memberRepository;
    private final FestivalRepository festivalRepository;
    private final FestivalLikeRepository festivalLikeRepository;

    @Transactional
    public void save(UserPrincipal principal, Long festivalId) {

        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(StatusCode.FESTIVAL_NOT_FOUND));

        log.info("[{}] 축제 북마크 저장 member: {}, festival: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getUsername(), festival.getTitle());

        festivalLikeRepository.save(FestivalLike.builder()
                .member(member)
                .festival(festival)
                .build());

    }

    @Transactional
    public void delete(UserPrincipal principal, Long festivalId){
        Member member = memberRepository.findById(principal.getMember().getId()).orElseThrow(
                () -> new NotValidMemberException(StatusCode.INVALID_MEMBER)
        );

        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomException(StatusCode.FESTIVAL_NOT_FOUND));

        FestivalLike festivalLike = festivalLikeRepository.findByMemberAndFestival(member, festival)
                .orElseThrow(() -> new CustomException(StatusCode.FESTIVAL_LIKE_NOT_FOUND));

        festivalLikeRepository.delete(festivalLike);
    }
}
