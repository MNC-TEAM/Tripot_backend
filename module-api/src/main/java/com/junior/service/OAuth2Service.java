package com.junior.service;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.junior.dto.jwt.LoginCreateJwtDto;
import com.junior.dto.member.CheckActiveMemberDto;
import com.junior.dto.oauth2.OAuth2Provider;
import com.junior.dto.oauth2.OAuth2UserInfo;
import com.junior.repository.member.MemberRepository;
import com.junior.security.JwtUtil;
import com.junior.strategy.oauth2.KakaoOAuth2LoginStrategy;
import com.junior.strategy.oauth2.OAuth2UserGenerator;
import com.junior.util.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final OAuth2UserGenerator oAuth2UserGenerator;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;


    public CheckActiveMemberDto oauth2Login(HttpServletResponse response, String code, OAuth2Provider provider) {

        OAuth2UserInfo userInfo = generateOAuth2UserInfo(code, provider);

        String username = userInfo.provider() + " " + userInfo.id();

        boolean existMember = memberRepository.existsByUsername(username);

        Member member = createMember(provider, existMember, username, userInfo);

        makeJWTs(member, response);


        return createResponse(response, member);
    }

    private OAuth2UserInfo generateOAuth2UserInfo(String code, OAuth2Provider provider) {
        //소셜 로그인 전략 설정
        if (provider == OAuth2Provider.KAKAO) {
            oAuth2UserGenerator.setOAuth2MemberStrategy(new KakaoOAuth2LoginStrategy());
        }

        //OAuth2 과정 진행 후 사용자 정보 받아오기
        OAuth2UserInfo userInfo = oAuth2UserGenerator.signUpOAuth2(code);
        return userInfo;
    }

    private void makeJWTs(Member member, HttpServletResponse response) {
        //JWT 생성
        LoginCreateJwtDto loginCreateJwtDto = LoginCreateJwtDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .role(member.getRole().toString())
                .requestTimeMs(LocalDateTime.now())
                .build();

        String accessToken = jwtUtil.createJwt(loginCreateJwtDto, "access");
        String refreshToken = jwtUtil.createJwt(loginCreateJwtDto, "refresh");
        log.info("[{}} JWT 토큰 생성 access: {}, refresh: {}", Thread.currentThread().getStackTrace()[1].getClassName(), accessToken, refreshToken);

        //redis에 refreshToken 저장하기((key, value): (token, username))
        //Bearer을 포함하지 않음
        redisUtil.setDataExpire(refreshToken, member.getUsername(), 8640_0000L * 30 * 6);


        //응답에 JWT 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Refresh_token", "Bearer " + refreshToken);
        log.info("[{}} 응답 헤더에 토큰 담기", Thread.currentThread().getStackTrace()[1].getClassName());
    }

    private Member createMember(OAuth2Provider provider, boolean existMember, String username, OAuth2UserInfo userInfo) {
        Member member;

        if (!existMember) {
            //PREACTIVE 상태 회원 생성
            log.info("[{}}] 신규 회원 생성 username: {}", Thread.currentThread().getStackTrace()[1].getClassName(), username, MemberStatus.PREACTIVE);

            member = Member.builder()
                    .nickname(userInfo.nickname())         //일단 전송 후 수정하는 방식
                    .username(username)
                    .role(MemberRole.USER)
                    //사용자 동의 정보: activeMember 기능에 추가
                    .signUpType(SignUpType.valueOf(provider.toString()))
                    .build();

            memberRepository.save(member);

        } else {
            //조건문에서 있는지 검증했음
            member = memberRepository.findByUsername(username).get();
            log.info("[{}}] 기존 회원 username: {}, status: {}", Thread.currentThread().getStackTrace()[1].getClassName(), username, member.getStatus());
        }
        return member;
    }

    private CheckActiveMemberDto createResponse(HttpServletResponse response, Member member) {
        //응답에 해당 회원의 추가정보 기입 여부 추가
        log.info("[{}} 응답에 해당 회원의 추가정보 기입 여부 추가", Thread.currentThread().getStackTrace()[1].getClassName());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        CheckActiveMemberDto checkActiveMemberDto;

        if (member.getStatus() == MemberStatus.PREACTIVE) {
            checkActiveMemberDto = new CheckActiveMemberDto(member.getNickname(), false);
        } else {
            checkActiveMemberDto = new CheckActiveMemberDto(member.getNickname(), true);
        }
        return checkActiveMemberDto;
    }
}
