package com.junior.strategy.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.junior.dto.oauth2.ApplePublicKeyResponse;
import com.junior.dto.oauth2.OAuth2LoginDto;
import com.junior.dto.oauth2.OAuth2Provider;
import com.junior.dto.oauth2.OAuth2UserInfo;
import com.junior.exception.CustomException;
import com.junior.exception.JwtErrorException;
import com.junior.exception.StatusCode;
import com.junior.security.JwtUtil;
import com.junior.security.generator.ApplePublicKeyGenerator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import static com.junior.dto.oauth2.OAuth2Provider.APPLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleOAuth2LoginStrategy implements OAuth2MemberStrategy {

    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final JwtUtil jwtUtil;
    @Value("${oauth2.apple.client-id}")
    private String clientId;
    @Value("${oauth2.apple.pk-host}")
    private String pkHost;

    //LoginDto에 토큰이 담겨있어야 함
    @Override
    public OAuth2UserInfo getOAuth2UserInfo(OAuth2LoginDto oAuth2LoginDto) {
        String appleAccountId = getAppleAccountId(oAuth2LoginDto.id());


        return OAuth2UserInfo.builder()
                .id(appleAccountId)
                .nickname(oAuth2LoginDto.nickname())
                .provider(APPLE).build();
    }

    @Override
    public boolean isTarget(OAuth2Provider oAuth2Provider) {
        return oAuth2Provider.equals(APPLE);
    }


    private String getAppleAccountId(String identityToken) {
        // jwt 헤더를 파싱한다.
        Map<String, String> headers = null;
        try {
            log.debug("[{}] 헤더 파싱", Thread.currentThread().getStackTrace()[1].getMethodName());
            headers = jwtUtil.parseHeaders(identityToken);
        } catch (JsonProcessingException e) {
            log.error("[{}] error: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage());
            throw new CustomException(StatusCode.OAUTH2_LOGIN_FAILURE);
        }
        // 공개키를 생성한다
        PublicKey publicKey = null;
        try {
            log.debug("[{}] 공개 키 생성", Thread.currentThread().getStackTrace()[1].getMethodName());
            publicKey = applePublicKeyGenerator.generatePublicKey(headers, getAppleAuthPublicKey());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("[{}] error: {}", Thread.currentThread().getStackTrace()[1].getMethodName(), e.getMessage());
            throw new CustomException(StatusCode.OAUTH2_LOGIN_FAILURE);
        }
        // 토큰의 signature를 검사하고 Claim 을 반환받는다.
        log.debug("[{}] 애플 토큰 검증 및 claim 생성", Thread.currentThread().getStackTrace()[1].getMethodName());
        Claims tokenClaims = jwtUtil.getTokenClaims(identityToken, publicKey);
        // Verify that the iss field contains https://appleid.apple.com
        if (!pkHost.equals(tokenClaims.getIssuer())) {
            log.error("[{}] error: issue is not correct", Thread.currentThread().getStackTrace()[1].getMethodName());
            throw new JwtErrorException(StatusCode.INVALID_TOKEN);
        }
        // aud 필드 검사
        if (!tokenClaims.getAudience().contains(clientId)) {
            log.error("[{}] error: aud is not correct", Thread.currentThread().getStackTrace()[1].getMethodName());
            throw new JwtErrorException(StatusCode.INVALID_TOKEN);
        }

        return tokenClaims.getSubject();
    }

    //애플에 직접 요청해서 PK 받아오기 -> 3개의 PK가 리턴
    private ApplePublicKeyResponse getAppleAuthPublicKey() {
        return WebClient.create(pkHost).get()
                .uri(uribuilder -> uribuilder
                        .scheme("https")
                        .path("/auth/keys")
                        .build(true))
                .retrieve()
                .bodyToMono(ApplePublicKeyResponse.class)
                .block();
    }
}
