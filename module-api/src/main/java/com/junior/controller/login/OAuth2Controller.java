package com.junior.controller.login;

import com.junior.controller.api.OAuth2Api;
import com.junior.dto.jwt.RefreshTokenDto;
import com.junior.dto.member.CheckActiveMemberDto;
import com.junior.dto.oauth2.OAuth2LoginDto;
import com.junior.dto.oauth2.OAuth2Provider;
import com.junior.exception.StatusCode;
import com.junior.response.CommonResponse;
import com.junior.service.login.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class OAuth2Controller implements OAuth2Api {

    private final OAuth2Service oAuth2Service;


    @PostMapping("/api/v2/login/oauth2/{provider}")
    public CommonResponse<CheckActiveMemberDto> oauth2Login(HttpServletResponse response, @RequestBody OAuth2LoginDto oAuth2LoginDto, @PathVariable("provider") String provider) {


        return CommonResponse.success(StatusCode.OAUTH2_LOGIN_SUCCESS, oAuth2Service.oauth2Login(response, oAuth2LoginDto, OAuth2Provider.valueOf(provider.toUpperCase())));

    }

    @PostMapping("/api/v1/logout")
    public CommonResponse<Boolean> logout(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {

        oAuth2Service.logout(refreshTokenDto);

        return CommonResponse.success(StatusCode.LOGOUT, null);
    }
}
