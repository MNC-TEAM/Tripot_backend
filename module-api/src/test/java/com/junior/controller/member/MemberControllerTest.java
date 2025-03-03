package com.junior.controller.member;

import com.junior.controller.BaseControllerTest;
import com.junior.domain.member.MemberStatus;
import com.junior.domain.member.SignUpType;
import com.junior.dto.member.*;
import com.junior.exception.StatusCode;
import com.junior.page.PageCustom;
import com.junior.security.UserPrincipal;
import com.junior.security.WithMockCustomAdmin;
import com.junior.security.WithMockCustomUser;
import com.junior.service.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends BaseControllerTest {

    @MockBean
    MemberService memberService;

    @Test
    @DisplayName("회원 활성화 - 응답이 반환되어야 함")
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
                .andExpect(jsonPath("$.customCode").value(StatusCode.ACTIVATE_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.ACTIVATE_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("닉네임 사용가능 여부 - 응답이 반환되어야 함")
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
                .andExpect(jsonPath("$.customCode").value(StatusCode.CHECK_NICKNAME_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.CHECK_NICKNAME_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(false));

    }

    @Test
    @DisplayName("회원 활성화 여부 확인 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomUser
    public void checkActiveMember() throws Exception {
        //given
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CheckActiveMemberDto checkActiveMemberDto = CheckActiveMemberDto.builder()
                .nickname("테스트사용자닉네임")
                .isActivate(true)
                .build();

        given(memberService.checkActiveMember(principal)).willReturn(checkActiveMemberDto);

        //when
        ResultActions actions = mockMvc.perform(
                get("/api/v1/members/check-activate")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("$.customCode").value(StatusCode.GET_MEMBER_ACTIVATE.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.GET_MEMBER_ACTIVATE.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.nickname").value("테스트사용자닉네임"))
                .andExpect(jsonPath("$.data.isActivate").value(true));

    }

    @Test
    @DisplayName("회원 정보 조회 - 응답에 조회한 회원 정보가 정상적으로 들어가야 함")
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
                .andExpect(jsonPath("$.customCode").value(StatusCode.GET_MEMBER_INFO.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.GET_MEMBER_INFO.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.nickname").value("nickname"))
                .andExpect(jsonPath("$.data.profileImageUrl").value("s3.com/profileImage"));


    }

    @Test
    @DisplayName("닉네임 변경 - 성공 응답이 반환되어야 함")
    @WithMockCustomUser
    void changeNickname() throws Exception {
        //given
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
                .andExpect(jsonPath("$.customCode").value(StatusCode.UPDATE_NICKNAME_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.UPDATE_NICKNAME_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));


    }

    @Test
    @WithMockCustomUser
    @DisplayName("회원 탈퇴 - 응답이 정상적으로 반환되어야 함")
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
                .andExpect(jsonPath("$.customCode").value(StatusCode.DELETE_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.DELETE_MEMBER.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data").value(nullValue()));


    }

    @Test
    @WithMockCustomUser
    @DisplayName("프로필 사진 수정 - 응답이 정상적으로 반환되어야 함")
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
                .andExpect(jsonPath("$.customCode").value(StatusCode.UPDATE_PROFILE_IMAGE_MEMBER.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.UPDATE_PROFILE_IMAGE_MEMBER.getCustomMessage()))
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

    @Test
    @DisplayName("회원 정보 조회 - 응답이 정상적으로 반환되어야 함")
    @WithMockCustomAdmin
    public void findMembers() throws Exception {
        //given
        PageRequest pageable = PageRequest.of(0, 20);
        String q = "";

        List<MemberListResponseDto> content = new ArrayList<>();

        content.add(
                MemberListResponseDto
                        .builder()
                        .id(1L)
                        .signUpType(SignUpType.KAKAO)
                        .status(MemberStatus.ACTIVE)
                        .nickname("닉네임")
                        .createdDate(LocalDateTime.MIN)
                        .build()
        );

        given(memberService.findMembers(any(Pageable.class), anyString())).willReturn(new PageCustom<>(content, pageable, 0));
        //when

        ResultActions actions = mockMvc.perform(
                get("/api/v1/admin/members")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions
                .andDo(print())
                .andExpect(jsonPath("$.customCode").value(StatusCode.GET_MEMBERS.getCustomCode()))
                .andExpect(jsonPath("$.customMessage").value(StatusCode.GET_MEMBERS.getCustomMessage()))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.pageable.number").value(1));


    }


}