package com.junior.service.login;

import com.junior.dto.jwt.LoginCreateJwtDto;
import com.junior.dto.jwt.RefreshTokenDto;
import com.junior.exception.JwtErrorException;
import com.junior.exception.StatusCode;
import com.junior.security.JwtUtil;
import com.junior.util.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;


    public void reissue(RefreshTokenDto refreshTokenDto, HttpServletResponse response) {

        String oldRefreshToken = refreshTokenDto.refreshToken().split(" ")[1];

        log.info("[{}] refresh token이 유효한 토큰인지 검증", Thread.currentThread().getStackTrace()[1].getMethodName());
        if (jwtUtil.isExpired(oldRefreshToken)) {
            //예외 처리: 만료된 refreshToken
            throw new JwtErrorException(StatusCode.EXPIRED_REFRESH_TOKEN);
        }

        //refresh 토큰인지 확인
        if (!jwtUtil.getCategory(oldRefreshToken).equals("refresh")) {
            //예외 처리: refresh 토큰이 아님
            throw new JwtErrorException(StatusCode.NOT_REFRESH_TOKEN);
        }

        //oldRefreshToken 존재 여부 확인
        if (!redisUtil.existsByKey(oldRefreshToken)) {
            //예외 처리: 존재하지 않는 토큰
            throw new JwtErrorException(StatusCode.TOKEN_NOT_EXIST);
        }

        log.info("[{}] refresh token 검증 완료, 새 토큰 생성", Thread.currentThread().getStackTrace()[1].getMethodName());
        String username = jwtUtil.getUsername(oldRefreshToken);

        LoginCreateJwtDto loginCreateJwtDto = LoginCreateJwtDto.builder()
                .id(jwtUtil.getId(oldRefreshToken))
                .username(jwtUtil.getUsername(oldRefreshToken))
                .role(jwtUtil.getRole(oldRefreshToken))
                .requestTimeMs(LocalDateTime.now())
                .build();

        String newAccessToken = jwtUtil.createJwt(loginCreateJwtDto, "access");
        String newRefreshToken = jwtUtil.createJwt(loginCreateJwtDto, "refresh");

        log.info("[{}} JWT 토큰 생성 access: {}, refresh: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), newAccessToken, newRefreshToken);

        //이전 토큰을 삭제하고 새 토큰 생성
        redisUtil.deleteData(oldRefreshToken);
        redisUtil.setDataExpire(newRefreshToken, username, 15778800);

        //새 토큰을 응답에 추가
        response.addHeader("Authorization", "Bearer " + newAccessToken);
        response.addHeader("Refresh_token", "Bearer " + newRefreshToken);


    }
}
