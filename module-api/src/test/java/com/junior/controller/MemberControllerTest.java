package com.junior.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.config.SecurityConfig;
import com.junior.controller.security.WithMockCustomUser;
import com.junior.dto.member.ActivateMemberDto;
import com.junior.dto.member.MemberInfoDto;
import com.junior.dto.member.UpdateNicknameDto;
import com.junior.security.JwtUtil;
import com.junior.security.UserPrincipal;
import com.junior.security.exceptionhandler.CustomAuthenticationEntryPoint;
import com.junior.service.member.MemberService;
import com.junior.service.security.UserDetailsServiceImpl;
import com.junior.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)     //JPA 관련 빈들을 mock으로 등록
@Import(SecurityConfig.class)
class MemberControllerTest {

    @MockBean
    private RedisUtil redisUtil;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("회원 활성화 완료 응답이 반환되어야 함")
    @WithMockCustomUser
    void activeMember() throws Exception {

        //given
        ActivateMemberDto activateMemberDto = new ActivateMemberDto("updatenick", "강원");

        String content = objectMapper.writeValueAsString(activateMemberDto);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/members/activate")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );


        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("MEMBER-SUCCESS-001"))
                .andExpect(jsonPath("$.customMessage").value("회원 활성화 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("닉네임 사용가능 여부 응답이 반환되어야 함")
    void checkNicknameValid() throws Exception {

        //given
        String nickname = "nickname";
        given(memberService.checkDuplicateNickname(nickname)).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/members/nicknames/check-valid")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", nickname)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("MEMBER-SUCCESS-002"))
                .andExpect(jsonPath("$.customMessage").value("닉네임 사용가능 여부"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(false));

    }

    @Test
    @DisplayName("응답에 조회한 회원 정보가 정상적으로 들어가야 함")
    @WithMockCustomUser
    void getMemberInfo() throws Exception {

        //given
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        MemberInfoDto memberInfoDto = new MemberInfoDto("nickname", "s3.com/profileImage");
        given(memberService.getMemberInfo(principal)).willReturn(memberInfoDto);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/members")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("MEMBER-SUCCESS-007"))
                .andExpect(jsonPath("$.customMessage").value("회원 정보 조회 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.nickname").value("nickname"))
                .andExpect(jsonPath("$.data.profileImageUrl").value("s3.com/profileImage"));


    }

    @Test
    @DisplayName("닉네임 변경 성공 응답이 반환되어야 함")
    @WithMockCustomUser
    void changeNickname() throws Exception {
        //given
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UpdateNicknameDto updateNicknameDto = new UpdateNicknameDto("newnick");
        String content = objectMapper.writeValueAsString(updateNicknameDto);

        //when
        ResultActions actions = mockMvc.perform(
                patch("/api/v1/members/nicknames")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("MEMBER-SUCCESS-005"))
                .andExpect(jsonPath("$.customMessage").value("회원 닉네임 변경 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));


    }

    @Test
    @WithMockCustomUser
    @DisplayName("회원 탈퇴 응답이 정상적으로 반환되어야 함")
    void deleteMember() throws Exception {

        //given

        //when
        ResultActions actions = mockMvc.perform(
                delete("/api/v1/members")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("MEMBER-SUCCESS-003"))
                .andExpect(jsonPath("$.customMessage").value("회원 삭제 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));


    }

    @Test
    @WithMockCustomUser
    @DisplayName("프로필 사진 수정 응답이 정상적으로 반환되어야 함")
    void changeProfileImage() throws Exception {

        //given
        MockMultipartFile profileImg = createMockMultipartFile("profileimg");

        //when
        ResultActions actions = mockMvc.perform(
                multipart(HttpMethod.PATCH, "/api/v1/members/profile-images")
                        .file(profileImg)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customCode").value("MEMBER-SUCCESS-006"))
                .andExpect(jsonPath("$.customMessage").value("회원 프로필 사진 변경 성공"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));

    }


    MockMultipartFile createMockMultipartFile(String name) {
        MockMultipartFile profileImg = new MockMultipartFile(
                name,
                "profiles.png",
                MediaType.IMAGE_PNG_VALUE,
                "thumbnail".getBytes()
        );

        return profileImg;

    }


}