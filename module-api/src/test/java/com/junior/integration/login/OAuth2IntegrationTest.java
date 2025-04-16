package com.junior.integration.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.controller.member.MemberController;
import com.junior.domain.member.Member;
import com.junior.dto.jwt.LoginCreateJwtDto;
import com.junior.dto.jwt.RefreshTokenDto;
import com.junior.dto.oauth2.OAuth2LoginDto;
import com.junior.exception.StatusCode;
import com.junior.integration.BaseIntegrationTest;
import com.junior.repository.member.MemberRepository;
import com.junior.security.JwtUtil;
import com.junior.security.WithMockCustomUser;
import com.junior.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static io.jsonwebtoken.Jwts.claims;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OAuth2IntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MemberController memberController;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Value("${oauth2.apple.client-id}")
    private String appleClientId;

    @BeforeEach
    void init() {

        Member preactiveTestMember = createPreactiveTestMember();
        Member activeKakaoTestMember = createActiveTestMember("KAKAO 1234");
        Member activeAppleTestMember = createActiveTestMember("APPLE 1234");

        memberRepository.save(preactiveTestMember);
        memberRepository.save(activeKakaoTestMember);
        memberRepository.save(activeAppleTestMember);

    }

    @Test
    @DisplayName("소셜 로그인 - 카카오 로그인 결과가 정상적으로 리턴되어야 함")
    public void oauth2LoginV2UsingKakao() throws Exception {
        //given
        String sampleAccess = "sample_access_token";
        String sampleRefresh = "sample_refresh_token";
        String kakaoProvider = "kakao";

        OAuth2LoginDto oAuth2LoginDto = OAuth2LoginDto.builder()
                .id("1234")
                .nickname("nickname")
                .build();

        given(jwtUtil.createJwt(any(LoginCreateJwtDto.class), eq("access"))).willReturn(sampleAccess);
        given(jwtUtil.createJwt(any(LoginCreateJwtDto.class), eq("refresh"))).willReturn(sampleRefresh);

        String content = objectMapper.writeValueAsString(oAuth2LoginDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v2/login/oauth2/{provider}", kakaoProvider)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.OAUTH2_LOGIN_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.OAUTH2_LOGIN_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.nickname").value(""))
                .andExpect(jsonPath("$.data.isActivate").value(false));

        verify(redisUtil).setDataExpire(anyString(), anyString(), anyLong());

    }

    @Test
    @DisplayName("소셜 로그인 - 애플 로그인 결과가 정상적으로 리턴되어야 함")
    public void oauth2LoginV2UsingApple() throws Exception {
        //given
        String sampleAccess = "sample_access_token";
        String sampleRefresh = "sample_refresh_token";
        String appleProvider = "apple";
        String identityToken = "identityToken";

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("kid", "rs0M3kOV9p");
        headerMap.put("alg", "RS256");

        Map<String, Object> userInfo = new HashMap<>();
        Set<String> aud = new LinkedHashSet<>();

        aud.add(appleClientId);

        userInfo.put("iss", "https://appleid.apple.com");
        userInfo.put("sub", "1234");
        userInfo.put("aud", aud);

        Claims claims = (Claims) ((ClaimsBuilder) claims().add(userInfo)).build();
        ;

        OAuth2LoginDto oAuth2LoginDto = OAuth2LoginDto.builder()
                .id(identityToken)
                .nickname("nickname")
                .build();

        given(jwtUtil.parseHeaders(identityToken)).willReturn(headerMap);
        given(jwtUtil.getTokenClaims(eq(identityToken), any(PublicKey.class))).willReturn(claims);
//        given(anySet().contains(any())).willReturn(true);
        given(jwtUtil.createJwt(any(LoginCreateJwtDto.class), eq("access"))).willReturn(sampleAccess);
        given(jwtUtil.createJwt(any(LoginCreateJwtDto.class), eq("refresh"))).willReturn(sampleRefresh);

        String content = objectMapper.writeValueAsString(oAuth2LoginDto);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v2/login/oauth2/{provider}", appleProvider)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value(StatusCode.OAUTH2_LOGIN_SUCCESS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.OAUTH2_LOGIN_SUCCESS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.nickname").value("테스트사용자닉네임"))
                .andExpect(jsonPath("$.data.isActivate").value(true));

        verify(redisUtil).setDataExpire(anyString(), anyString(), anyLong());

    }

    @Test
    @DisplayName("로그아웃 - 결과가 정상적으로 리턴되어야 함")
    @WithMockCustomUser
    public void logout() throws Exception {
        //given
        String sampleRefresh = "Bearer sample_refresh_token";

        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(sampleRefresh);
        String content = objectMapper.writeValueAsString(refreshTokenDto);

        given(jwtUtil.getCategory(anyString())).willReturn("refresh");

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/v1/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("$.customCode").value(StatusCode.LOGOUT.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.LOGOUT.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verify(redisUtil).deleteData("sample_refresh_token");

    }
}
