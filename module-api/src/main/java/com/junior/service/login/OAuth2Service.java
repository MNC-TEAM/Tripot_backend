package com.junior.service.login;

import com.junior.domain.member.Member;
import com.junior.domain.member.MemberRole;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.junior.dto.jwt.LoginCreateJwtDto;
import com.junior.dto.jwt.RefreshTokenDto;
import com.junior.dto.member.CheckActiveMemberDto;
import com.junior.dto.oauth2.OAuth2LoginDto;
import com.junior.dto.oauth2.OAuth2Provider;
import com.junior.dto.oauth2.OAuth2UserInfo;
import com.junior.exception.CustomException;
import com.junior.exception.JwtErrorException;
import com.junior.exception.StatusCode;
import com.junior.repository.member.MemberRepository;
import com.junior.security.JwtUtil;
import com.junior.strategy.oauth2.OAuth2MemberStrategy;
import com.junior.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {


    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final List<OAuth2MemberStrategy> oAuth2MemberStrategies;


    /**
     * OAuth2 과정을 프론트 단에서 처리
     * @param response
     * @param oAuth2LoginDto
     * @param provider
     * @return
     */
    public CheckActiveMemberDto oauth2Login(HttpServletResponse response, OAuth2LoginDto oAuth2LoginDto, OAuth2Provider provider) {


        OAuth2UserInfo userInfo = generateOAuth2UserInfo(oAuth2LoginDto, provider);

        String username = userInfo.provider() + " " + userInfo.id();

        boolean existMember = memberRepository.existsByUsername(username);

        Member member = createMember(provider, existMember, username, userInfo);

        makeJWTs(member, response);


        return createResponse(response, member);
    }

    public void logout(RefreshTokenDto refreshTokenDto) {


        if (!refreshTokenDto.refreshToken().startsWith("Bearer ")) {
            throw new JwtErrorException(StatusCode.INVALID_TOKEN);
        }

        String refreshToken = refreshTokenDto.refreshToken().split(" ")[1];


        try {
            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            String category = jwtUtil.getCategory(refreshToken);

            if (!category.equals("refresh")) {
                throw new JwtErrorException(StatusCode.NOT_REFRESH_TOKEN);
            }
        } catch (ExpiredJwtException e) {
            // 토큰 만료 여부 확인
            throw new JwtErrorException(StatusCode.EXPIRED_REFRESH_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            //유효하지 않은 토큰
            throw new JwtErrorException(StatusCode.INVALID_TOKEN);
        }


        redisUtil.deleteData(refreshToken);

    }


    private OAuth2UserInfo generateOAuth2UserInfo(OAuth2LoginDto oAuth2LoginDto, OAuth2Provider provider) {
        //소셜 로그인 전략 설정
        log.info("[{}] 소셜 로그인 전략 설정 및 정보 추출", Thread.currentThread().getStackTrace()[1].getMethodName());
        return oAuth2MemberStrategies.stream()
                .filter(oAuth2MemberStrategy -> oAuth2MemberStrategy.isTarget(provider))
                .findAny()
                .orElseThrow(() -> new CustomException(StatusCode.OAUTH2_LOGIN_FAILURE))
                .getOAuth2UserInfo(oAuth2LoginDto);
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
        redisUtil.setDataExpire(refreshToken, member.getUsername(), 15778800);


        //응답에 JWT 추가
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addHeader("Refresh_token", "Bearer " + refreshToken);
        log.info("[{}} 응답 헤더에 토큰 담기", Thread.currentThread().getStackTrace()[1].getClassName());
    }

    private Member createMember(OAuth2Provider provider, boolean existMember, String username, OAuth2UserInfo userInfo) {
        Member member;

        if (!existMember) {
            //PREACTIVE 상태 회원 생성
            log.info("[{}}] 신규 회원 생성 username: {}", Thread.currentThread().getStackTrace()[1].getClassName(), username);

            member = Member.builder()
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
