package com.junior.strategy.oauth2;

import com.junior.dto.oauth2.OAuth2LoginDto;
import com.junior.dto.oauth2.OAuth2Provider;
import com.junior.dto.oauth2.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.junior.dto.oauth2.OAuth2Provider.KAKAO;

@Slf4j
@Component
public class KakaoOAuth2LoginStrategy implements OAuth2MemberStrategy {

    @Override
    public OAuth2UserInfo getOAuth2UserInfo(OAuth2LoginDto oAuth2LoginDto) {
        log.debug("[{}] 카카오 유저 정보 생성", Thread.currentThread().getStackTrace()[1].getMethodName());
        return OAuth2UserInfo.builder()
                .id(oAuth2LoginDto.id())
                .nickname(oAuth2LoginDto.nickname())
                .provider(KAKAO)
                .build();
    }

    @Override
    public boolean isTarget(OAuth2Provider oAuth2Provider) {
        return oAuth2Provider.equals(KAKAO);
    }

}
